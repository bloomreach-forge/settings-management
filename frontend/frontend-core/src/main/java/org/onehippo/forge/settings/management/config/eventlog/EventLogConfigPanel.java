/*
 * Copyright 2013-2015 Hippo B.V. (http://www.onehippo.com)
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

package org.onehippo.forge.settings.management.config.eventlog;

import javax.jcr.RepositoryException;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.settings.management.FeatureConfigPanel;
import org.onehippo.forge.settings.management.config.CMSFeatureConfig;


public class EventLogConfigPanel extends FeatureConfigPanel {
    private static final long serialVersionUID = 1L;
    private EventLogConfigModel detachableEventLogConfig = new EventLogConfigModel();

    public EventLogConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        add(new TextField("cronexpression", new PropertyModel(detachableEventLogConfig, "cronexpression")));
        add(new TextField("minutestolive", new PropertyModel(detachableEventLogConfig, "minutestolive")));
        add(new TextField("maxitems", new PropertyModel(detachableEventLogConfig, "maxitems")));
    }

    public void save() {
        CMSFeatureConfig config = detachableEventLogConfig.getConfig();

        try {
            config.save();
        } catch (RepositoryException e) {
            error("An error occurred while trying to save event log configuration: " + e);
        }
    }

    public void cancel() {
        // do nothing.
    }
}
