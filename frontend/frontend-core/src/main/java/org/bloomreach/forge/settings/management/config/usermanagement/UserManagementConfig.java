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

package org.bloomreach.forge.settings.management.config.usermanagement;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.hippoecm.frontend.plugins.cms.admin.users.ListUsersPlugin;
import org.bloomreach.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UserManagementConfig
 */
public class UserManagementConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(UserManagementConfig.class);
    private Boolean userCreationEnabled;
    private transient Node node;

    public UserManagementConfig(Node configNode) {
        init(configNode);
    }

    private void init(Node configNode){
        this.node = configNode;
        try {
            if(node.hasProperty(ListUsersPlugin.USER_CREATION_ENABLED_KEY)) {
                userCreationEnabled = node.getProperty(ListUsersPlugin.USER_CREATION_ENABLED_KEY).getBoolean();
            } else {
                //default
                userCreationEnabled = true;
            }
        } catch (RepositoryException e) {
            log.error("Error: {}", e);
        }
    }

    public Boolean isUserCreationEnabled() {
        return userCreationEnabled;
    }

    public void setUserCreationEnabled(final Boolean userCreationEnabled) {
        this.userCreationEnabled = userCreationEnabled;
    }

    @Override
    public void save() throws RepositoryException {
        node.setProperty(ListUsersPlugin.USER_CREATION_ENABLED_KEY, userCreationEnabled);
        node.getSession().save();
    }

}
