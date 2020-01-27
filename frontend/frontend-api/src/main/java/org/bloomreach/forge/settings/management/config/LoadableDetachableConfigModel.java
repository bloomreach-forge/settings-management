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

package org.bloomreach.forge.settings.management.config;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.wicket.model.LoadableDetachableModel;
import org.hippoecm.frontend.session.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public abstract class LoadableDetachableConfigModel<T> extends LoadableDetachableModel<T> {

    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public CMSFeatureConfig getConfig() {
        return (CMSFeatureConfig)getObject();
    }

    protected Node getConfigNode(final String path) {
        try {
            final Session jcrSession = UserSession.get().getJcrSession();
            if (jcrSession.nodeExists(path)) {
                return jcrSession.getNode(path);
            }
        } catch (RepositoryException e) {
            log.error("Error trying to retrieve configuration node at {}", path);
        }

        log.info("No configuration found at {}", path);
        return null;
    }

}
