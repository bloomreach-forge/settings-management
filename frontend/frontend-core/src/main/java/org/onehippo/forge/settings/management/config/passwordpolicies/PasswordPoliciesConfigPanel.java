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

package org.onehippo.forge.settings.management.config.passwordpolicies;

import javax.jcr.RepositoryException;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.settings.management.FeatureConfigPanel;

public class PasswordPoliciesConfigPanel extends FeatureConfigPanel {
    private static final long serialVersionUID = 1L;
    private final HippoSecurityConfigModel hippoSecurityConfigModel;
    private final ChangePasswordConfigModel changePasswordConfigModel;
    private final PasswordValidationConfigModel passwordValidationConfigModel;

    public PasswordPoliciesConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        hippoSecurityConfigModel = new HippoSecurityConfigModel();
        changePasswordConfigModel = new ChangePasswordConfigModel();
        passwordValidationConfigModel = new PasswordValidationConfigModel();

        add(new TextField("hippo-security-maxage-passwords", new PropertyModel(hippoSecurityConfigModel,"maxAgeForPasswords")));
        add(new TextField("hippo-password-expiration-notification-period", new PropertyModel(changePasswordConfigModel,"passwordexpirationnotificationdays")));
        add(new TextField("hippo-password-validation-required-strength",new PropertyModel(passwordValidationConfigModel, "requiredStrength")));
        add(new TextField("hippo-password-minimal-length",new PropertyModel(passwordValidationConfigModel, "minimalLengthPassword")));
        add(new TextField("hippo-passwords-number-of-previous-passwords", new PropertyModel(passwordValidationConfigModel,"numberofpreviouspasswords")));
    }

    public void save() {
        HippoSecurityConfig hippoSecurityConfig = hippoSecurityConfigModel.getObject();
        ChangePasswordConfig changePasswordConfig = changePasswordConfigModel.getObject();
        PasswordValidationConfig passwordValidationConfig = passwordValidationConfigModel.getObject();

        try {
            //TODO calling session save multiple times is not very efficient.
            hippoSecurityConfig.save();
            changePasswordConfig.save();
            passwordValidationConfig.save();
        } catch (RepositoryException e) {
            error("An error occurred while trying to save event log configuration: " + e);
        }
    }

    public void cancel() {
        // do nothing.
    }
}