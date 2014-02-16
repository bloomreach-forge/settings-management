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

import java.io.Serializable;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for social media configurations for the social media sharing functionality.
 * @author Jeroen Reijn
 */
public class SocialMediaService implements CMSFeatureConfig, Serializable {
    private static final Logger log = LoggerFactory.getLogger(SocialMediaService.class);
    private static final String PROP_DISPLAY_NAME = "display.name";
    private static final String PROP_ENABLED = "enabled";
    private static final String PROP_SHARE_URL = "share.url";
    private String name;
    private Boolean enabled;
    private String url;
    private transient Node node;

    public SocialMediaService(Node node) {
        this.node = node;
        try{
            if(node.hasProperty(PROP_DISPLAY_NAME)) {
                this.name = node.getProperty(PROP_DISPLAY_NAME).getString();
            }
            if(node.hasProperty(PROP_ENABLED)) {
                this.enabled = node.getProperty(PROP_ENABLED).getBoolean();
            }
            if(node.hasProperty(PROP_SHARE_URL)) {
                this.url = node.getProperty(PROP_SHARE_URL).getString();
            }
        } catch (RepositoryException e) {
            log.warn("Warn: {}",e);
        }
    }

    public SocialMediaService(String name, boolean enabled, String url){
        this.name = name;
        this.enabled = enabled;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public void save() throws RepositoryException {
        if(node!=null) {
            if(node.getName().equals("unknown")){
                node.getSession().move(node.getPath(), node.getParent().getPath() + "/" + this.name.toLowerCase());
            }
            node.setProperty(PROP_ENABLED,this.enabled);
            node.setProperty(PROP_SHARE_URL,this.url);
            node.setProperty(PROP_DISPLAY_NAME,this.name);
            node.getSession().save();
        }
    }
}
