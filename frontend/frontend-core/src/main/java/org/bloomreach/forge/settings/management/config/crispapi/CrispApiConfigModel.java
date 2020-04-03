/*
 * Copyright 2020 Bloomreach Inc. (http://www.bloomreach.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bloomreach.forge.settings.management.config.crispapi;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.bloomreach.forge.settings.management.config.LoadableDetachableConfigModel;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.session.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrispApiConfigModel extends LoadableDetachableConfigModel<CrispApiConfig> {

    private static Logger log = LoggerFactory.getLogger(CrispApiConfigModel.class);

    private final IPluginConfig pluginConfig;

    public CrispApiConfigModel(final IPluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    public Node getCrispConfigNode() {
        try {
            if (UserSession.get().getJcrSession().nodeExists(CrispApiConfigConstants.CRISP_CONFIG_PATH)) {
                return super.getConfigNode(CrispApiConfigConstants.CRISP_CONFIG_PATH);
            }
        } catch (RepositoryException e) {
            log.error("Cannot read crisp resource space configuration node at {}.",
                    CrispApiConfigConstants.CRISP_CONFIG_PATH, e);
        }

        return null;
    }

    protected CrispApiConfig load() {
        return new CrispApiConfig(pluginConfig, getCrispConfigNode());
    }
}
