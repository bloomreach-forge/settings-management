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

package org.bloomreach.forge.settings.management.config.passwordpolicies;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.bloomreach.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ChangePasswordConfig
 * ChangePasswordConfig
 */
public class ChangePasswordConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(ChangePasswordConfig.class);
    private static final String PROP_PASSWORD_EXPIRATION_NOTIFICATION_DAYS = "passwordexpirationnotificationdays";
    private transient Node configNode;
    private String passwordexpirationnotificationdays;

    public ChangePasswordConfig(final Node configNode) {
        init(configNode);
    }

    private void init(final Node configNode) {
        this.configNode = configNode;
        try {
            if(configNode.hasProperty(PROP_PASSWORD_EXPIRATION_NOTIFICATION_DAYS)) {
                this.passwordexpirationnotificationdays = configNode.getProperty(PROP_PASSWORD_EXPIRATION_NOTIFICATION_DAYS).getString();
            }
        } catch (RepositoryException e) {
            log.warn("Unable to load password configuration: {}",e);
        }
    }

    public String getPasswordexpirationnotificationdays() {
        return passwordexpirationnotificationdays;
    }

    public void setPasswordexpirationnotificationdays(final String passwordexpirationnotificationdays) {
        this.passwordexpirationnotificationdays = passwordexpirationnotificationdays;
    }

    @Override
    public void save() throws RepositoryException {
        if(passwordexpirationnotificationdays!=null) {
            configNode.setProperty(PROP_PASSWORD_EXPIRATION_NOTIFICATION_DAYS,passwordexpirationnotificationdays);
        }
        configNode.getSession().save();
    }
}
