/*
 * Copyright 2013-2015 Hippo B.V. (http://www.onehippo.com)
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
package org.onehippo.forge.settings.management.config.eventlog;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.onehippo.forge.settings.management.config.SchedulerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class EventLogConfig implements CMSFeatureConfig {

    private final static Logger logger = LoggerFactory.getLogger(EventLogConfig.class);

    private static final String ATTR_MINUTES_TO_LIVE = "minutestolive";
    private static final String ATTR_MAXITEMS = "maxitems";

    private transient Node jobNode;

    private String cronexpression;
    private Long minutestolive;
    private Long maxitems;

    public EventLogConfig(final Node jobNode) {
        init(jobNode);
    }

    private void init(final Node node) {
        this.jobNode = node;
        try {
            maxitems = SchedulerUtils.getAttributeAsLong(jobNode, ATTR_MAXITEMS, -1l);
            minutestolive = SchedulerUtils.getAttributeAsLong(jobNode, ATTR_MINUTES_TO_LIVE, -1l);
            cronexpression = SchedulerUtils.getCronExpression(jobNode);
        } catch (RepositoryException e) {
            logger.error("Error: {}", e);
        }
    }

    public String getCronExpression() {
        return cronexpression;
    }

    public void setCronexpression(final String cronexpression) {
        this.cronexpression = cronexpression;
    }

    public Long getMinutestolive() {
        return minutestolive;
    }

    public void setMinutestolive(final Long minutestolive) {
        this.minutestolive = minutestolive;
    }

    public Long getMaxitems() {
        return maxitems;
    }

    public void setMaxitems(final Long maxitems) {
        this.maxitems = maxitems;
    }

    public void save() throws RepositoryException {
        SchedulerUtils.setCronExpression(jobNode, getCronExpression());
        SchedulerUtils.setAttribute(jobNode, ATTR_MAXITEMS, getMaxitems().toString());
        SchedulerUtils.setAttribute(jobNode, ATTR_MINUTES_TO_LIVE, getMinutestolive().toString());
        jobNode.getSession().save();
    }


}
