/*
 * Copyright 2013 Hippo B.V. (http://www.onehippo.com)
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

package org.onehippo.forge.settings.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.CssResourceReference;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.cms.admin.AdminBreadCrumbPanel;
import org.hippoecm.frontend.plugins.standards.ClassResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Panel for displaying the available settings.
 * The available settings are categorized in tabs.
 * @author Jeroen Reijn, Tobi Jeger
 */
public class SettingsPanel extends AdminBreadCrumbPanel {

    private static final long serialVersionUID = 1L;
    private final static Logger logger = LoggerFactory.getLogger(SettingsPanel.class);

    private static Comparator<IPluginConfig> comparator;
    private static final String FEATURE_CONFIG_PROPERTY = "featureConfigClass";
    static {
        comparator = new Comparator<IPluginConfig>() {
            @Override
            public int compare(final IPluginConfig o1, final IPluginConfig o2) {
            double s1 = 0;
            double s2 = 0;
                s1 = o1.getAsDouble("sequence");
                s2 = o2.getAsDouble("sequence");
                if (s1 > s2) { return 1; }
            if (s1 < s2) { return -1; }
            return 0;
            }
        };
    }

    public SettingsPanel(final String componentId, final IBreadCrumbModel breadCrumbModel, final IPluginContext context, final IPluginConfig config) {
        super(componentId,breadCrumbModel);

        // Load the tabs configuration from the repository
        List<Tab> tabConfigs = new ArrayList<Tab>();
        try {
            final Set<IPluginConfig> pluginConfigSet = config.getPluginConfigSet();
            final Iterator<IPluginConfig> pluginConfigIterator = pluginConfigSet.iterator();
            if(pluginConfigIterator.hasNext()) {
                tabConfigs = loadTabs(pluginConfigIterator.next().getPluginConfigSet());
            }
        } catch (RepositoryException ex) {
            logger.warn("An exception occurred while trying to load the tabs: {}",ex);
            return;
        }

        // Create the tabs according to the configuration
        if (tabConfigs.size() > 0) {
            List<ITab> tabs = new ArrayList<ITab>();

            for (Tab tabConfig : tabConfigs) {
                final List<FeatureConfigPanel> mcs = new ArrayList<FeatureConfigPanel>();

                for (IPluginConfig iPluginConfig : tabConfig.getFeatures()) {
                    try {
                        // Instantiate a FeatureConfigPanel given the classname (clazz)
                        mcs.add((FeatureConfigPanel)Class.forName(iPluginConfig.getString(FEATURE_CONFIG_PROPERTY))
                                .getConstructor(IPluginContext.class, IPluginConfig.class)
                                .newInstance(context, iPluginConfig));
                    } catch (Exception ex) {
                        logger.warn("An exception occurred while trying instantiate the feature plugin: {}", ex);
                    }
                }

                tabs.add(new AbstractTab(new Model<>(tabConfig.getTitle()))
                {
                    private static final long serialVersionUID = 1L;
                    private TabPanel panel;
                    @Override
                    public Panel getPanel(String panelId)
                    {
                        if (panel == null) {
                            panel = new TabPanel(panelId, breadCrumbModel, context, config, mcs);
                        }
                        return panel;
                    }
                });
            }
            add(new AjaxTabbedPanel<>("tabs", tabs));
        } else {
            add(new Label("tabs", new ClassResourceModel("admin-settings-no-tabs", SettingsPanel.class)));
        }
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new CssResourceReference(SettingsPanel.class, "settings-panel.css")));
    }


    @Override
    public IModel<String> getTitle(final Component component) {
        return new ClassResourceModel("admin-settings-title",SettingsPanel.class);
    }

    /**
     * Load and sort all tabs to be instantiated.
     *
     *
     * @param tabConfig node holding the tab configuration
     * @return Sorted list of tabs, each holding a sorted list of features.
     * @throws RepositoryException if unexpected repository content is encountered
     */
    protected List<Tab> loadTabs(final Set<IPluginConfig> tabConfig) throws RepositoryException {
        List<Tab> tabs = null;

        final Iterator<IPluginConfig> tabIterator = tabConfig.iterator();
        final List<IPluginConfig> tabConfigs = new ArrayList<IPluginConfig>();
        while (tabIterator.hasNext()) {
            tabConfigs.add(tabIterator.next());
        }

        tabs = sortTabsBySequence(tabs, tabConfigs);
        return tabs;
    }

    private List<Tab> sortTabsBySequence(List<Tab> tabs, final List<IPluginConfig> tabConfigs) throws RepositoryException {
        if (tabConfigs.size() > 0) {
            Collections.sort(tabConfigs, comparator);

            tabs = new ArrayList<Tab>();
            for (IPluginConfig pluginConfig : tabConfigs) {
                Tab tab = loadTab(pluginConfig);
                if (tab != null) {
                    tabs.add(tab);
                }
            }
        }
        return tabs;
    }

    /**
     * Load a single tab, represented by a JCR node. The node must have a 'title' String property,
     * specifying the title of the tab, and multiple child nodes, each representing a configurable
     * feature. Each child node must have a 'featureConfigClass' String property, specifying which
     * class to instantiate in order to configure the feature, and it may have a 'sequence' double
     * property to determine the ordering of features inside the tab.
     *
     *
     * @param pluginConfig JCR node representing the a single tab.
     * @return Tab class holding the sorted data loaded from JCR.
     * @throws javax.jcr.RepositoryException if vital properties are missing.
     */
    protected Tab loadTab(final IPluginConfig pluginConfig) throws RepositoryException {
        Tab tab = null;
        String tabTitle = pluginConfig.getString("title","");

        final List<IPluginConfig> featureConfigs = new ArrayList<IPluginConfig>();
        final Iterator fit = pluginConfig.getPluginConfigSet().iterator();

        while (fit.hasNext()) {
            IPluginConfig config = (IPluginConfig) fit.next();
            if (config.containsKey(FEATURE_CONFIG_PROPERTY)) {
                featureConfigs.add(config);
            }
            //i18n for tab labels
            if (config.getName().contains("hippo:translation")) {
                String language = "";
                if (config.containsKey("hippo:language")) {
                    language = config.getString("hippo:language");
                }
                if (language.equals(getLocale().getLanguage())) {
                    tabTitle = config.getString("hippo:message");
                }
            }
        }

        if (featureConfigs.size() > 0) {
            Collections.sort(featureConfigs, comparator);
            tab = new Tab(tabTitle);
            for (IPluginConfig featureConfig : featureConfigs) {
                tab.addFeature(featureConfig);
            }
        }
        return tab;
    }

    /**
     * Simple bean specifying a single tab.
     */
    private class Tab {
        String title;
        List<IPluginConfig> features;

        Tab(String title) {
            this.title = title;
            features = new ArrayList<IPluginConfig>();
        }

        public void addFeature(IPluginConfig feature) {
            features.add(feature);
        }

        public List<IPluginConfig> getFeatures() {
            return features;
        }

        public String getTitle() {
            return title;
        }
    }
}
