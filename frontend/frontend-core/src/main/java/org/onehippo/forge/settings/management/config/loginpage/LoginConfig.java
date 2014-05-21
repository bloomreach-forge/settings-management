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

package org.onehippo.forge.settings.management.config.loginpage;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
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
    public static final String PROP_HTTP_ONLY_COOKIES = "use.httponly.cookies";
    public static final String PROP_SECURE_COOKIES = "use.secure.cookies";
    public static final String PROP_REMEMBER_ME_COOKIE_AGE = "rememberme.cookie.maxage";
    public static final String PROP_HAL_COOKIE_AGE = "hal.cookie.maxage";

    private static final int DEFAULT_NUMBER_OF_ATTEMPTS_BEFORE_CAPTCHA_IS_SHOWN = 3;

    private transient Node node;

    private Boolean autoComplete;
    private Boolean useCaptcha;
    private Boolean useHttpOnlyCookies;
    private Boolean useSecureCookies;
    private int numberOfAttemptBeforeCaptchaIsShown;
    private int rememberMeCookieAge;
    private int hippoAutoLoginCookieAge;

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
            if (node.hasProperty(PROP_HTTP_ONLY_COOKIES)) {
                this.useHttpOnlyCookies = node.getProperty(PROP_HTTP_ONLY_COOKIES).getBoolean();
            }
            if (node.hasProperty(PROP_SECURE_COOKIES)) {
                this.useSecureCookies = node.getProperty(PROP_SECURE_COOKIES).getBoolean();
            }
            if (node.hasProperty(PROP_NUMBER_OF_ATTEMPTS_BEFORE_CAPTCHA_IS_SHOWN)) {
                Property nrOfAttemptsProperty = node.getProperty(PROP_NUMBER_OF_ATTEMPTS_BEFORE_CAPTCHA_IS_SHOWN);
                this.numberOfAttemptBeforeCaptchaIsShown = Integer.parseInt(nrOfAttemptsProperty.getString());
            }
            if (node.hasProperty(PROP_REMEMBER_ME_COOKIE_AGE)) {
                Property rememberMeCookieAgeProperty = node.getProperty(PROP_REMEMBER_ME_COOKIE_AGE);
                this.rememberMeCookieAge = Integer.parseInt(rememberMeCookieAgeProperty.getString());
            }
            if (node.hasProperty(PROP_HAL_COOKIE_AGE)) {
                Property halCookieAgeProperty = node.getProperty(PROP_HAL_COOKIE_AGE);
                this.hippoAutoLoginCookieAge = Integer.parseInt(halCookieAgeProperty.getString());
            }
        } catch (RepositoryException e) {
            log.error("Error: {}", e);
        }
    }

    public Integer getRememberMeCookieAge() {
        return rememberMeCookieAge;
    }

    public void setRememberMeCookieAge(final int rememberMeCookieAge) {
        this.rememberMeCookieAge = rememberMeCookieAge;
    }

    public Integer getHippoAutoLoginCookieAge() {
        return hippoAutoLoginCookieAge;
    }

    public void setHippoAutoLoginCookieAge(final int hippoAutoLoginCookieAge) {
        this.hippoAutoLoginCookieAge = hippoAutoLoginCookieAge;
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

    public Boolean getUseHttpOnlyCookies() {
        return useHttpOnlyCookies;
    }

    public void setUseHttpOnlyCookies(final Boolean useHttpOnlyCookies) {
        this.useHttpOnlyCookies = useHttpOnlyCookies;
    }

    public Boolean getUseSecureCookies() {
        return useSecureCookies;
    }

    public void setUseSecureCookies(final Boolean useSecureCookies) {
        this.useSecureCookies = useSecureCookies;
    }

    public void save() throws RepositoryException {
        node.setProperty(PROP_AUTOCOMPLETE, autoComplete);
        node.setProperty(PROP_USE_CAPTCHA, useCaptcha);
        node.setProperty(PROP_HTTP_ONLY_COOKIES, useHttpOnlyCookies);
        node.setProperty(PROP_SECURE_COOKIES, useSecureCookies);
        node.setProperty(PROP_NUMBER_OF_ATTEMPTS_BEFORE_CAPTCHA_IS_SHOWN, numberOfAttemptBeforeCaptchaIsShown);
        node.setProperty(PROP_REMEMBER_ME_COOKIE_AGE, rememberMeCookieAge);
        node.setProperty(PROP_HAL_COOKIE_AGE, hippoAutoLoginCookieAge);
        node.getSession().save();
    }

}
