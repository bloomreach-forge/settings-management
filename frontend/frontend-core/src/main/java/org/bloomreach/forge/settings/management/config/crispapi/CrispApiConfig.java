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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.bloomreach.forge.settings.management.config.CMSFeatureConfig;
import org.bloomreach.forge.settings.management.config.urlrewriter.UrlRewriterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrispApiConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(UrlRewriterConfig.class);

    public static final String CONFIGURATION_PATH = "/hippo:configuration/hippo:modules/crispregistry/hippo:moduleconfig";

    private final transient Node node;

    private List<CrispResourceSpace> crispResourceSpaces = new LinkedList<>();

    public CrispApiConfig(final Node configNode) {

        this.node = configNode;

        if (this.node != null) {
            // FIXME
            crispResourceSpaces.clear();

            CrispResourceSpace resourceSpace = new CrispResourceSpace("brsm", "Bloomreach S&M");
            resourceSpace.addProperty(new CrispResourceSpaceProperty("prop1.1", "value1.1"));
            resourceSpace.addProperty(new CrispResourceSpaceProperty("prop1.2", "value1.2"));
            crispResourceSpaces.add(resourceSpace);

            resourceSpace = new CrispResourceSpace("commercetools", "commrecetools");
            resourceSpace.addProperty(new CrispResourceSpaceProperty("prop2.1", "value2.1"));
            resourceSpace.addProperty(new CrispResourceSpaceProperty("prop2.2", "value2.2"));
            resourceSpace.addProperty(new CrispResourceSpaceProperty("prop2.3", "value2.3"));
            crispResourceSpaces.add(resourceSpace);

            resourceSpace = new CrispResourceSpace("elasticpath", "Elastic Path");
            resourceSpace.addProperty(new CrispResourceSpaceProperty("prop3.1", "value3.1"));
            resourceSpace.addProperty(new CrispResourceSpaceProperty("prop3.2", "value3.2"));
            resourceSpace.addProperty(new CrispResourceSpaceProperty("prop3.3", "value3.3"));
            resourceSpace.addProperty(new CrispResourceSpaceProperty("prop3.4", "value3.4"));
            crispResourceSpaces.add(resourceSpace);
        }
    }

    @Override
    public void save() throws RepositoryException {
        // TODO
        
        node.getSession().save();
    }

    public Boolean hasConfiguration() {
        return node != null;
    }

    public List<CrispResourceSpace> getCrispResourceSpaces() {
        return crispResourceSpaces;
    }

    public CrispResourceSpace getCrispResourceSpaceByName(final String resourceSpaceName) {
        return crispResourceSpaces.stream().filter(r -> StringUtils.equals(resourceSpaceName, r.getResourceSpaceName()))
                .findFirst().orElse(null);
    }

    public void removeCrispResourceSpace(final String resourceSpaceName) {
        for (Iterator<CrispResourceSpace> it = crispResourceSpaces.iterator(); it.hasNext(); ) {
            final CrispResourceSpace resourceSpace = it.next();

            if (StringUtils.equals(resourceSpaceName, resourceSpace.getResourceSpaceName())) {
                it.remove();
                break;
            }
        }
    }

    public void addCrispResourceSpace(final CrispResourceSpace resourceSpace) {
        crispResourceSpaces.add((CrispResourceSpace) resourceSpace.clone());
    }

    public List<String> getAvailableBackendTypeNames() {
        return Arrays.asList("brsm", "commercetools", "elasticpath");
    }
}
