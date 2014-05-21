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

package org.onehippo.forge.settings.management.config.brokenlinks;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class BrokenlinksCheckerConfig implements CMSFeatureConfig {

    private static final Logger log = LoggerFactory.getLogger(BrokenlinksCheckerConfig.class);

    public static final String PROP_CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String PROP_NR_THREADS = "nrOfThreads";
    public static final String PROP_SOCKET_TIMEOUT = "socketTimeout";
    public static final String PROP_STARTPATH = "startPath";

    public static final Long DEFAULT_CONNECTION_TIMEOUT = 10000l;
    public static final Integer DEFAULT_NR_THREADS = 10;
    public static final Long DEFAULT_SOCKET_TIMEOUT = 10000l;
    public static final String DEFAULT_START_PATH = "/content/documents";

    private transient Node node;
    private Long connectionTimeout;
    private Long socketTimeout;
    private String startPath;
    private String startPathUUID;
    private int nrOfThreads;

    public BrokenlinksCheckerConfig(final Node configNode) {
        this.node = configNode;
        try {
            if(node.hasProperty(PROP_CONNECTION_TIMEOUT)) {
                connectionTimeout = node.getProperty(PROP_CONNECTION_TIMEOUT).getLong();
            } else {
                connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
            }
            if(node.hasProperty(PROP_NR_THREADS)) {
                nrOfThreads = Integer.parseInt(node.getProperty(PROP_NR_THREADS).getString());
            } else {
                nrOfThreads = DEFAULT_NR_THREADS;
            }
            if(node.hasProperty(PROP_SOCKET_TIMEOUT)) {
                socketTimeout = node.getProperty(PROP_SOCKET_TIMEOUT).getLong();
            } else {
                socketTimeout = DEFAULT_SOCKET_TIMEOUT;
            }
            if(node.hasProperty(PROP_STARTPATH)) {
                startPath = node.getProperty(PROP_STARTPATH).getString();
            } else {
                startPath = DEFAULT_START_PATH;
            }
            startPathUUID = getIdentifierForPath(startPath);
        } catch (RepositoryException e) {
            log.error("Error: {}",e);
        }
    }

    private String getIdentifierForPath(String path) throws RepositoryException {
        return node.getSession().getNode(path).getIdentifier();
    }

    private String getPathForIdentifier(String uuid)  {
        try {
            return node.getSession().getNodeByIdentifier(uuid).getPath();
        } catch (RepositoryException e) {
            log.error("Error: {}", e);
        }
        return null;
    }

    @Override
    public void save() throws RepositoryException {
        node.setProperty(PROP_CONNECTION_TIMEOUT,connectionTimeout);
        node.setProperty(PROP_NR_THREADS,nrOfThreads);
        node.setProperty(PROP_SOCKET_TIMEOUT,socketTimeout);
        node.setProperty(PROP_STARTPATH,startPath);
        node.getSession().save();
    }

    public Long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final Long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Long getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(final Long socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getStartPath() {
        return startPath;
    }

    public void setStartPath(final String startPath) {
        this.startPath = startPath;
    }

    public Integer getNrOfThreads() {
        return nrOfThreads;
    }

    public void setNrOfThreads(final int nrOfThreads) {
        this.nrOfThreads = nrOfThreads;
    }

    public String getStartPathUUID() {
        return startPathUUID;
    }

    public void setStartPathUUID(final String startPathUUID) {
        this.startPathUUID = startPathUUID;
        setStartPath(getPathForIdentifier(startPathUUID));
    }
}
