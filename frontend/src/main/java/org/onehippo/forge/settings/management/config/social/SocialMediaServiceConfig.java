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

package org.onehippo.forge.settings.management.config.social;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class SocialMediaServiceConfig implements CMSFeatureConfig {

    private static final Logger log = LoggerFactory.getLogger(SocialMediaServiceConfig.class);
    List<SocialMediaService> services;
    private transient Node configNode;

    public SocialMediaServiceConfig(final Node configNode) {
        services = new ArrayList<SocialMediaService>();
        init(configNode);
    }

    private void init(final Node configNode) {
        this.configNode = configNode;
        try {
            NodeIterator nodeIterator = configNode.getNodes();
            while(nodeIterator.hasNext()) {
                Node node = nodeIterator.nextNode();
                services.add(new SocialMediaService(node));
            }
        } catch (RepositoryException e) {
            log.warn("Warn: {}",e);
        }
    }

    public List<SocialMediaService> getServices() {
        return services;
    }

    public void setServices(final List<SocialMediaService> services) {
        this.services = services;
    }

    public SocialMediaService addService(String name) {
        try {
            final Node serviceNode = configNode.addNode(name.toLowerCase(), "frontend:pluginconfig");
            return new SocialMediaService(serviceNode);
        } catch (RepositoryException e) {
            log.warn("Warn: {}", e);
        }
        return null;
    }
    @Override
    public void save() throws RepositoryException {
        for(SocialMediaService service : services) {
            service.save();
        }
        configNode.getSession().save();
    }
}
