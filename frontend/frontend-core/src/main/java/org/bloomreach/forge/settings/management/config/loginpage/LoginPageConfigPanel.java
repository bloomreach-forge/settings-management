/*
 * Copyright 2013-2020 Bloomreach Inc. (http://www.bloomreach.com)
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

package org.bloomreach.forge.settings.management.config.loginpage;

import javax.jcr.RepositoryException;

import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.bloomreach.forge.settings.management.FeatureConfigPanel;

public class LoginPageConfigPanel extends FeatureConfigPanel {
    private LoginConfigModel loginConfigModel;

    public LoginPageConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        loginConfigModel = new LoginConfigModel();

        RadioGroup captchaRadioGroup = new RadioGroup("login-use-captcha", new PropertyModel(loginConfigModel, "useCaptcha"));
        captchaRadioGroup.add(new Radio<>("captcha-off", new Model<>(Boolean.FALSE)));
        captchaRadioGroup.add(new Radio<>("captcha-on", new Model<>(Boolean.TRUE)));
        add(captchaRadioGroup);

        add(new TextField("numberOfAttemptBeforeCaptchaIsShown", new PropertyModel(loginConfigModel, "numberOfAttemptBeforeCaptchaIsShown")));
    }

    public void save() {
        LoginConfig loginConfig = loginConfigModel.getObject();

        try {
            loginConfig.save();
        } catch (RepositoryException e) {
            error("An error occurred while trying to save login page configuration: " + e);
        }
    }

    public void cancel() {
        // do nothing.
    }
}
