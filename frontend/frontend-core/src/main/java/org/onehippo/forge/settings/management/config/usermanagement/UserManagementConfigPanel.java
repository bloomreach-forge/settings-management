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

package org.onehippo.forge.settings.management.config.usermanagement;

import javax.jcr.RepositoryException;

import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.settings.management.FeatureConfigPanel;

public class UserManagementConfigPanel extends FeatureConfigPanel {

    private static final long serialVersionUID = 1L;
    private UserManagementConfigModel userManagementConfigModel;

    public UserManagementConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        userManagementConfigModel = new UserManagementConfigModel();

        RadioGroup allowUserCreationRadioGroup = new RadioGroup("userlist-allow-usercreation", new PropertyModel(userManagementConfigModel,"userCreationEnabled"));
        allowUserCreationRadioGroup.add(new Radio("usercreation-disabled",new Model(Boolean.FALSE)));
        allowUserCreationRadioGroup.add(new Radio("usercreation-enabled",new Model(Boolean.TRUE)));
        add(allowUserCreationRadioGroup);
    }

    public void save() {
        UserManagementConfig userManagementConfig = userManagementConfigModel.getObject();

        try {
            userManagementConfig.save();
        } catch (RepositoryException e) {
            error("An error occurred while trying to save event log configuration: " + e);
        }
    }

    public void cancel() {
        // do nothing.
    }
}
