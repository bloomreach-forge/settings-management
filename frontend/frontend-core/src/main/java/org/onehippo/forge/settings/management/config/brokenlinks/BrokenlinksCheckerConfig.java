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

package org.onehippo.forge.settings.management.config.brokenlinks;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.onehippo.forge.settings.management.config.SchedulerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class BrokenlinksCheckerConfig implements CMSFeatureConfig {

    private static final Logger log = LoggerFactory.getLogger(BrokenlinksCheckerConfig.class);

    public static final String ATTR_CONNECTION_TIMEOUT = "connectionTimeout";
    public static final String ATTR_SOCKET_TIMEOUT = "socketTimeout";
    public static final String ATTR_NR_HTTP_THREADS = "nrHttpThreads";
    public static final String ATTR_STARTPATH = "startPath";
    public static final String ATTR_URLEXCLUDES = "urlExcludes";

    public static final Long DEFAULT_CONNECTION_TIMEOUT = 10000l;
    public static final Long DEFAULT_NR_THREADS = 10l;
    public static final Long DEFAULT_SOCKET_TIMEOUT = 10000l;
    public static final String DEFAULT_START_PATH = "/content/documents";

    private transient Node jobNode;
    private Long connectionTimeout;
    private Long socketTimeout;
    private Long nrHttpThreads;
    private String startPath;
    private String startPathUUID;
    private String urlExcludes;
    private boolean enabled;
    private String cronExpression;


    public BrokenlinksCheckerConfig(final Node configNode) {
        this.jobNode = configNode;
        try {
            connectionTimeout = SchedulerUtils.getAttributeAsLong(jobNode, ATTR_CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
            socketTimeout = SchedulerUtils.getAttributeAsLong(jobNode, ATTR_SOCKET_TIMEOUT, DEFAULT_SOCKET_TIMEOUT);
            nrHttpThreads = SchedulerUtils.getAttributeAsLong(jobNode, ATTR_NR_HTTP_THREADS, DEFAULT_NR_THREADS);
            startPath = SchedulerUtils.getAttribute(jobNode, ATTR_STARTPATH, DEFAULT_START_PATH);
            startPathUUID = getIdentifierForPath(startPath);
            urlExcludes = SchedulerUtils.getAttribute(jobNode, ATTR_URLEXCLUDES, null);
            cronExpression = SchedulerUtils.getCronExpression(jobNode);
            enabled = SchedulerUtils.isEnabled(jobNode);
        } catch (RepositoryException e) {
            log.error("Error: {}",e);
        }
    }

    private String getIdentifierForPath(String path) throws RepositoryException {
        return jobNode.getSession().getNode(path).getIdentifier();
    }

    private String getPathForIdentifier(String uuid)  {
        try {
            return jobNode.getSession().getNodeByIdentifier(uuid).getPath();
        } catch (RepositoryException e) {
            log.error("Error: {}", e);
        }
        return null;
    }

    @Override
    public void save() throws RepositoryException {
        SchedulerUtils.setAttribute(jobNode, ATTR_CONNECTION_TIMEOUT, getConnectionTimeout());
        SchedulerUtils.setAttribute(jobNode, ATTR_SOCKET_TIMEOUT, getSocketTimeout());
        SchedulerUtils.setAttribute(jobNode, ATTR_NR_HTTP_THREADS, getNrHttpThreads());
        SchedulerUtils.setAttribute(jobNode, ATTR_STARTPATH, getStartPath());
        SchedulerUtils.setAttribute(jobNode, ATTR_URLEXCLUDES, getUrlExcludes());
        jobNode.getSession().save();
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

    public Long getNrHttpThreads() {
        return nrHttpThreads;
    }

    public void setNrHttpThreads(final long nrHttpThreads) {
        this.nrHttpThreads = nrHttpThreads;
    }

    public String getStartPathUUID() {
        return startPathUUID;
    }

    public void setStartPathUUID(final String startPathUUID) {
        this.startPathUUID = startPathUUID;
        setStartPath(getPathForIdentifier(startPathUUID));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(final String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getUrlExcludes() {
        return urlExcludes;
    }

    public void setUrlExcludes(final String urlExcludes) {
        this.urlExcludes = urlExcludes;
    }
}
