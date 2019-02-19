/*
 * Copyright 2014-2019 BloomReach Inc. (http://www.bloomreach.com)
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

package org.onehippo.forge.settings.management.config.formdata;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.onehippo.forge.settings.management.config.ConfigUtil;
import org.onehippo.forge.settings.management.config.SchedulerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 *
 * TODO: attribute batchsize
 */
public class FormdataConfig implements CMSFeatureConfig {

    private final static Logger logger = LoggerFactory.getLogger(FormdataConfig.class);

    public static final String ATTR_MINUTES_TO_LIVE = "minutestolive";
    public static final String ATTR_EXCLUDE_PATHS = "excludepaths";

    private transient Node jobNode;

    private String cronexpression;
    private Long minutesToLive;
    private List<String> excludepaths;
    private boolean dataAvailable = true;

    public FormdataConfig(final Node jobNode) {
        if(jobNode != null) {
            init(jobNode);
        } else {
            dataAvailable = false;
        }
    }

    private void init(final Node node) {
        this.jobNode = node;
        try {
            this.cronexpression = SchedulerUtils.getCronExpression(jobNode);
            minutesToLive = SchedulerUtils.getAttributeAsLong(jobNode, ATTR_MINUTES_TO_LIVE, -1l);
            final String excludepathsAttr = SchedulerUtils.getAttribute(jobNode, ATTR_EXCLUDE_PATHS, "/formdata/permanent/");
            if (excludepathsAttr != null) {
                excludepaths = Arrays.asList(excludepathsAttr.split("\\|"));
            }
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

    public Long getMinutesToLive() {
        return minutesToLive;
    }

    public void setMinutesToLive(final Long minutesToLive) {
        this.minutesToLive = minutesToLive;
    }

    public List<String> getExcludepaths() {
        return excludepaths;
    }

    public void setExcludepaths(final List<String> excludepaths) {
        this.excludepaths = excludepaths;
    }

    private String getExludePathsAttr() {
        final StringBuilder sb = new StringBuilder();
        if (excludepaths != null && !excludepaths.isEmpty()) {
            final Iterator<String> iterator = excludepaths.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next());
                if (iterator.hasNext()) {
                    sb.append("|");
                }
            }
        }
        return sb.toString();
    }
    public void save() throws RepositoryException {
        SchedulerUtils.setCronExpression(jobNode, getCronExpression());
        SchedulerUtils.setAttribute(jobNode, ATTR_MINUTES_TO_LIVE, getMinutesToLive().toString());
        SchedulerUtils.setAttribute(jobNode, ATTR_EXCLUDE_PATHS, getExludePathsAttr());
        jobNode.getSession().save();
    }

    public Boolean isDataAvailable() {
        return this.dataAvailable;
    }
}
