/*
 * Copyright 2013-2019 BloomReach Inc. (http://www.bloomreach.com)
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

package org.bloomreach.forge.settings.management;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;

/**
 * FeatureConfigPanel is the template class to create Wicket panels for managing the
 * configuration of a CMS or site feature through the Manage Settings view of the CMS
 * Control Panel.
 * Multiple ManageableConfigurations are (typically) grouped on a TabPanel with a certain
 * theme. The TabPanel implements the form for managing all fields of the grouped feature
 * configurations, each form has a save and a cancel button.
 *
 * If you want to implement such a panel, extend this class and add all feature-related form
 * fields to the Manageable Configuration. Typically, the configuration data is handled through
 * a specialization of the LoadableDetachableConfigModel.
 */
public abstract class FeatureConfigPanel extends Panel {
    private static final long serialVersionUID = 1L;
    private IPluginContext context;
    private IPluginConfig config;

    public FeatureConfigPanel(IPluginContext context, IPluginConfig config, IModel title) {
        super("feature-config-panel");

        this.context = context;
        this.config = config;

        // set feature title
        add(new Label("feature-title", title));
    }

    /**
     * Invoked when the user clicks on the tab's save button.
     */
    public abstract void save();

    /**
     * Invoked when the user clicks on the tab's cancel button.
     */
    public abstract void cancel();

    protected IPluginContext getContext() {
        return context;
    }

    protected IPluginConfig getConfig() {
        return config;
    }
}
