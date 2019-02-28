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

package org.bloomreach.forge.settings.management.config.loginpage;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.bloomreach.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class LoginConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(LoginConfig.class);

    public static final String PROP_AUTOCOMPLETE = "signin.form.autocomplete";
    public static final String PROP_USE_CAPTCHA = "use.captcha";
    public static final String PROP_NUMBER_OF_ATTEMPTS_BEFORE_CAPTCHA_IS_SHOWN = "show.captcha.after.how.many.times";

    private static final int DEFAULT_NUMBER_OF_ATTEMPTS_BEFORE_CAPTCHA_IS_SHOWN = 3;

    private transient Node node;

    private Boolean autoComplete;
    private Boolean useCaptcha;
    private int numberOfAttemptBeforeCaptchaIsShown;

    public LoginConfig(Node configNode) {
        init(configNode);
    }

    private void init(final Node configNode) {
        this.node = configNode;
        try {
            if (node.hasProperty(PROP_AUTOCOMPLETE)) {
                this.autoComplete = node.getProperty(PROP_AUTOCOMPLETE).getBoolean();
            }
            if (node.hasProperty(PROP_USE_CAPTCHA)) {
                this.useCaptcha = node.getProperty(PROP_USE_CAPTCHA).getBoolean();
            }
            if (node.hasProperty(PROP_NUMBER_OF_ATTEMPTS_BEFORE_CAPTCHA_IS_SHOWN)) {
                Property nrOfAttemptsProperty = node.getProperty(PROP_NUMBER_OF_ATTEMPTS_BEFORE_CAPTCHA_IS_SHOWN);
                this.numberOfAttemptBeforeCaptchaIsShown = Integer.parseInt(nrOfAttemptsProperty.getString());
            }
        } catch (RepositoryException e) {
            log.error("Error: {}", e);
        }
    }

    public Integer getNumberOfAttemptBeforeCaptchaIsShown() {
        return numberOfAttemptBeforeCaptchaIsShown;
    }

    public void setNumberOfAttemptBeforeCaptchaIsShown(final int numberOfAttemptBeforeCaptchaIsShown) {
        this.numberOfAttemptBeforeCaptchaIsShown = numberOfAttemptBeforeCaptchaIsShown;
    }

    public Boolean getAutoComplete() {
        return autoComplete;
    }

    public void setAutoComplete(final Boolean autoComplete) {
        this.autoComplete = autoComplete;
    }

    public Boolean getUseCaptcha() {
        return useCaptcha;
    }

    public void setUseCaptcha(final Boolean useCaptcha) {
        this.useCaptcha = useCaptcha;
    }

    public void save() throws RepositoryException {
        node.setProperty(PROP_AUTOCOMPLETE, autoComplete);
        node.setProperty(PROP_USE_CAPTCHA, useCaptcha);
        node.setProperty(PROP_NUMBER_OF_ATTEMPTS_BEFORE_CAPTCHA_IS_SHOWN, numberOfAttemptBeforeCaptchaIsShown);
        node.getSession().save();
    }

}
