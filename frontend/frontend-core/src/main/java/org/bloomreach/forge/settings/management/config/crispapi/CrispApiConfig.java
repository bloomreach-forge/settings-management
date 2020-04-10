/*
 * Copyright 2020 Bloomreach Inc. (http://www.bloomreach.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bloomreach.forge.settings.management.config.crispapi;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bloomreach.forge.settings.management.config.CMSFeatureConfig;
import org.bloomreach.forge.settings.management.config.urlrewriter.UrlRewriterConfig;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.repository.util.JcrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * CRISP API Configuration loading/saving and templates.
 */
public class CrispApiConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(UrlRewriterConfig.class);

    /**
     * The XPATH expression to find any optional &ltmeta /&gt; element, which can help inferring the the backend
     * type name in the existing beans XML.
     */
    private static final String XPATH_META_BACKEND_TYPE = "//meta[@key='"
            + CrispApiConfigConstants.META_BACKEND_TYPE_KEY + "']/@value";

    /**
     * The CRISP ResourceResolver configuration node.
     */
    private transient Node crispConfigNodeInternal;

    /**
     * The CRISP ResourceResolver configuration node path.
     */
    private String crispConfigNodePath;

    /**
     * All available CRISP resource space templates.
     */
    private final List<CrispResourceSpace> crispResourceSpaceTemplates;

    /**
     * The current CRISP resource spaces, initially loaded from CRISP resource space configuration, and including
     * new added resource spaces, not persisted to JCR yet, by the user.
     */
    private final List<CrispResourceSpace> currentCrispResourceSpaces;

    /**
     * Removed CRISP resource spaces by user, not persisted to JCR yet.
     */
    private final List<CrispResourceSpace> removedCrispResourceSpaces;

    public CrispApiConfig(final IPluginConfig pluginConfig, final Node crispConfigNode) {
        this.crispConfigNodeInternal = crispConfigNode;

        crispResourceSpaceTemplates = new LinkedList<>();
        currentCrispResourceSpaces = new LinkedList<>();
        removedCrispResourceSpaces = new LinkedList<>();

        final String[] templateBackendTypeNames = getNonNullStringArrayFromPluginConfig(pluginConfig,
                CrispApiConfigConstants.PROP_CRISP_TEMPLATES);

        for (String backendTypeName : templateBackendTypeNames) {
            String key = String.format(CrispApiConfigConstants.PROP_CRISP_TEMPLATE_DEPENDS_FORMAT, backendTypeName);
            final String[] dependentFQCNs = getNonNullStringArrayFromPluginConfig(pluginConfig, key);

            if (isAnyDependentClassMissing(dependentFQCNs)) {
                log.info(
                        "CRISP resource space template cannot be available because all dependent class(es) are not available: {}",
                        StringUtils.join(dependentFQCNs, ", "));
                continue;
            }

            final CrispResourceSpace crispTemplate = new CrispResourceSpace(backendTypeName, backendTypeName);

            key = String.format(CrispApiConfigConstants.PROP_CRISP_TEMPLATE_BEANDEFINITION_FORMAT, backendTypeName);
            final String beansDef = pluginConfig.getString(key);
            crispTemplate.setBeansDefinition(beansDef);

            key = String.format(CrispApiConfigConstants.PROP_CRISP_TEMPLATE_PROPNAMES_FORMAT, backendTypeName);
            final String[] propNames = getNonNullStringArrayFromPluginConfig(pluginConfig, key);
            key = String.format(CrispApiConfigConstants.PROP_CRISP_TEMPLATE_PROPVALUES_FORMAT, backendTypeName);
            final String[] propValues = getNonNullStringArrayFromPluginConfig(pluginConfig, key);
            key = String.format(CrispApiConfigConstants.PROP_CRISP_TEMPLATE_CONCEALED_PROPNAMES_FORMAT, backendTypeName);
            final Set<String> concealedPropNameSet = new HashSet<>(
                    Arrays.asList(getNonNullStringArrayFromPluginConfig(pluginConfig, key)));

            try {
                final List<CrispResourceSpaceProperty> props = parseCrispResourceSpaceProperties(backendTypeName,
                        propNames, propValues, concealedPropNameSet);
                crispTemplate.setProperties(props);
            } catch (Exception pe) {
                log.error("CRISP property names/values are unmatched for the backend, '{}'.", backendTypeName, pe);
            }

            crispResourceSpaceTemplates.add(crispTemplate);
        }

        if (crispConfigNode != null) {
            try {
                crispConfigNodePath = crispConfigNode.getPath();
            } catch (RepositoryException e) {
                log.error("Cannot get the path of the crisp config node.", e);
            }

            refreshCurrentCrispResourceSpaces();
        }
    }

    @Override
    public void save() throws RepositoryException {
        final Node crispConfigNode = getCrispConfigNode();

        if (crispConfigNode == null) {
            log.warn("CRISP resource space configuration node doesn't exist.");
            return;
        }

        try {
            for (CrispResourceSpace resourceSpace : removedCrispResourceSpaces) {
                final String resourceSpaceName = resourceSpace.getResourceSpaceName();

                if (crispConfigNode.hasNode(resourceSpaceName)) {
                    final Node node = crispConfigNode.getNode(resourceSpaceName);
                    node.remove();
                } else {
                    log.debug("No crisp resource to remove by the name, '{}'. Probably a temporary addition.",
                            resourceSpaceName);
                }
            }

            for (CrispResourceSpace resourceSpace : currentCrispResourceSpaces) {
                final String resourceSpaceName = resourceSpace.getResourceSpaceName();
                final String backendTypeName = resourceSpace.getBackendTypeName();

                final CrispResourceSpace template = getCrispResourceSpaceTemplateByBackendTypeName(backendTypeName);

                if (template == null) {
                    log.error("CRISP resource template not found by the backend type name, '{}'.", backendTypeName);
                    continue;
                }

                final Node targetNode = (crispConfigNode.hasNode(resourceSpaceName))
                        ? crispConfigNode.getNode(resourceSpaceName)
                        : crispConfigNode.addNode(resourceSpaceName, "crisp:resourceresolver");
                updateCrispResourceSpaceConfigNode(targetNode, resourceSpace);
            }

            crispConfigNode.getSession().save();

            removedCrispResourceSpaces.clear();
            refreshCurrentCrispResourceSpaces();
        } catch (RepositoryException e) {
            log.error("Failed to save crisp resource resolvers.", e);
        }
    }

    public Boolean hasConfiguration() {
        return StringUtils.isNotBlank(crispConfigNodePath);
    }

    public List<CrispResourceSpace> getCurrentCrispResourceSpaces() {
        return currentCrispResourceSpaces;
    }

    public CrispResourceSpace getCurrentCrispResourceSpaceByResourceSpaceName(final String resourceSpaceName) {
        if (StringUtils.isBlank(resourceSpaceName)) {
            return null;
        }

        return currentCrispResourceSpaces.stream()
                .filter(r -> StringUtils.equals(resourceSpaceName, r.getResourceSpaceName())).findFirst().orElse(null);
    }

    public void removeCurrentCrispResourceSpace(final String resourceSpaceName) {
        for (Iterator<CrispResourceSpace> it = currentCrispResourceSpaces.iterator(); it.hasNext();) {
            final CrispResourceSpace resourceSpace = it.next();

            if (StringUtils.equals(resourceSpaceName, resourceSpace.getResourceSpaceName())) {
                it.remove();
                removedCrispResourceSpaces.add(resourceSpace);
                break;
            }
        }
    }

    public void addCurrentCrispResourceSpace(final CrispResourceSpace resourceSpace) {
        currentCrispResourceSpaces.add((CrispResourceSpace) resourceSpace.clone());
    }

    public List<CrispResourceSpace> getRemovedCrispResourceSpaces() {
        return removedCrispResourceSpaces;
    }

    public List<CrispResourceSpace> getCrispResourceSpaceTemplates() {
        return Collections.unmodifiableList(crispResourceSpaceTemplates);
    }

    public CrispResourceSpace getCrispResourceSpaceTemplateByBackendTypeName(final String backendTypeName) {
        if (StringUtils.isBlank(backendTypeName)) {
            return null;
        }

        return crispResourceSpaceTemplates.stream()
                .filter(r -> StringUtils.equals(backendTypeName, r.getBackendTypeName())).findFirst().orElse(null);
    }

    private void refreshCurrentCrispResourceSpaces() {
        currentCrispResourceSpaces.clear();

        final Node crispConfigNode = getCrispConfigNode();

        if (crispConfigNode == null) {
            log.debug("CRISP resource space configuration node doesn't exist.");
            return;
        }

        try {
            for (NodeIterator nodeIt = crispConfigNode.getNodes(); nodeIt.hasNext();) {
                final Node node = nodeIt.nextNode();

                if (node == null) {
                    continue;
                }

                final String resourceSpaceName = node.getName();
                final String backendTypeName = inferBackendTypeName(node);
                final CrispResourceSpace template = getCrispResourceSpaceTemplateByBackendTypeName(backendTypeName);
                final CrispResourceSpace crispResourceSpace = new CrispResourceSpace(resourceSpaceName,
                        backendTypeName);

                crispResourceSpace.setBeansDefinition(
                        JcrUtils.getStringProperty(node, "crisp:beandefinition", StringUtils.EMPTY));

                final String[] propNames = JcrUtils.getMultipleStringProperty(node, "crisp:propnames",
                        ArrayUtils.EMPTY_STRING_ARRAY);
                final String[] propValues = JcrUtils.getMultipleStringProperty(node, "crisp:propvalues",
                        ArrayUtils.EMPTY_STRING_ARRAY);

                final Set<String> concealedPropNames = (template != null) ? template.getConcealedPropertyNameSet()
                        : null;
                final List<CrispResourceSpaceProperty> props = parseCrispResourceSpaceProperties(backendTypeName,
                        propNames, propValues, concealedPropNames);
                crispResourceSpace.setProperties(props);

                currentCrispResourceSpaces.add(crispResourceSpace);
            }
        } catch (RepositoryException e) {
            log.error("Failed to load crisp resource resolver configurations.");
        }
    }

    private String[] getNonNullStringArrayFromPluginConfig(final IPluginConfig pluginConfig, final String key) {
        final String[] values = pluginConfig.getStringArray(key);
        return (values != null) ? values : ArrayUtils.EMPTY_STRING_ARRAY;
    }

    private List<CrispResourceSpaceProperty> parseCrispResourceSpaceProperties(final String backendTypeName,
            final String[] propNames, final String[] propValues, final Set<String> concealedPropNameSet) {
        final List<CrispResourceSpaceProperty> propList = new ArrayList<>();

        for (int i = 0; i < propNames.length; i++) {
            final String propName = propNames[i];
            final String propValue = (propValues.length > i) ? propValues[i] : "";
            final boolean concealed = (concealedPropNameSet != null && concealedPropNameSet.contains(propName));
            final CrispResourceSpaceProperty crispProp = new CrispResourceSpaceProperty(propName, propValue, concealed);
            propList.add(crispProp);
        }

        if (propNames.length > propValues.length) {
            log.error(
                    "CRISP property names/values are unmatched for the backend, '{}'. Names: {}, values: {}. Empty string values set for unmatched property names.",
                    backendTypeName, propNames, propValues);
        }

        return propList;
    }

    private String inferBackendTypeName(final Node resourceResolverNode) throws RepositoryException {
        final String beansDef = JcrUtils.getStringProperty(resourceResolverNode, "crisp:beandefinition", null);

        if (StringUtils.isNotBlank(beansDef)) {
            try {
                final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                final DocumentBuilder builder = factory.newDocumentBuilder();
                final Document doc = builder.parse(new InputSource(new StringReader(beansDef)));
                final XPathFactory xpathfactory = XPathFactory.newInstance();
                final XPath xpath = xpathfactory.newXPath();
                final XPathExpression expr = xpath.compile(XPATH_META_BACKEND_TYPE);
                final Object result = expr.evaluate(doc, XPathConstants.STRING);

                if (result instanceof String) {
                    return (String) result;
                }
            } catch (Exception e) {
                log.error("Cannot parse or read metadata from '{}/@crisp:beandefinition'.",
                        resourceResolverNode.getPath(), e);
            }
        }

        final String resourceSpaceName = resourceResolverNode.getName();

        for (CrispResourceSpace template : crispResourceSpaceTemplates) {
            if (resourceSpaceName.equals(template.getResourceSpaceName())) {
                return template.getBackendTypeName();
            }
        }

        return null;
    }

    private void updateCrispResourceSpaceConfigNode(final Node target, final CrispResourceSpace source)
            throws RepositoryException {
        final String oldBackendTypeName = inferBackendTypeName(target);
        final String[] oldPropNames = JcrUtils.getMultipleStringProperty(target, "crisp:propnames",
                ArrayUtils.EMPTY_STRING_ARRAY);
        final String[] oldPropValues = JcrUtils.getMultipleStringProperty(target, "crisp:propvalues",
                ArrayUtils.EMPTY_STRING_ARRAY);
        final String oldBeansDef = JcrUtils.getStringProperty(target, "crisp:beandefinition", StringUtils.EMPTY);

        final String newBackendTypeName = source.getBackendTypeName();
        final String[] newPropNames = source.getPropertyNames();
        final String[] newPropValues = source.getPropertyValues();
        final String newBeansDef = source.getBeansDefinition();

        if (!Arrays.equals(oldPropNames, newPropNames)) {
            target.setProperty("crisp:propnames", newPropNames);
        }

        if (!Arrays.equals(oldPropValues, newPropValues)) {
            target.setProperty("crisp:propvalues", newPropValues);
        }

        // if the backend type was not changed, then do not replace the old beans xml as it could have been customized.
        if (StringUtils.isBlank(oldBeansDef) || !StringUtils.equals(oldBackendTypeName, newBackendTypeName)) {
            target.setProperty("crisp:beandefinition", newBeansDef);
        }
    }

    private boolean isAnyDependentClassMissing(final String[] dependentFQCNs) {
        try {
            for (String fqcn : dependentFQCNs) {
                if (StringUtils.isNotBlank(fqcn)) {
                    Class.forName(fqcn);
                }
            }
        } catch (ClassNotFoundException e) {
            return true;
        }

        return false;
    }

    private Node getCrispConfigNode() {
        try {
            if (crispConfigNodeInternal == null && StringUtils.isNotBlank(crispConfigNodePath)) {
                crispConfigNodeInternal = UserSession.get().getJcrSession().getNode(crispConfigNodePath);
            }
        } catch (RepositoryException e) {
            log.error("Cannot get the crisp resource space configuratio node at {}.", crispConfigNodePath);
        }

        return crispConfigNodeInternal;
    }
}
