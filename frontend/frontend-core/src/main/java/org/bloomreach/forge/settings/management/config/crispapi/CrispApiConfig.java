/*
 * Copyright 2020 Bloomreach Inc. (http://www.bloomreach.com)
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
package org.bloomreach.forge.settings.management.config.crispapi;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.bloomreach.forge.settings.management.config.CMSFeatureConfig;
import org.bloomreach.forge.settings.management.config.urlrewriter.UrlRewriterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrispApiConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(UrlRewriterConfig.class);

    public static final String CONFIGURATION_PATH = "/hippo:configuration/hippo:modules/crispregistry/hippo:moduleconfig";

    private final transient Node node;

    public Boolean hasConfiguration() {
        return node != null;
    }

    public CrispApiConfig(final Node configNode) {

        this.node = configNode;

        if (this.node != null) {
            // TODO
        }
    }

    @Override
    public void save() throws RepositoryException {
        // TODO
        //node.getSession().save();
    }
}
