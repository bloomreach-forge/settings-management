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

package org.onehippo.forge.settings.management.config.eventlog;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class EventLogConfig implements CMSFeatureConfig {

    private final static Logger logger = LoggerFactory.getLogger(EventLogConfig.class);

    public static final String PROP_CRONEXPRESSION = "cronexpression";
    public static final String PROP_KEEP_ITEMS_FOR = "keepitemsfor";
    public static final String PROP_MAXITEMS = "maxitems";

    private transient Node node;

    private String cronexpression;
    private Long keepitemsfor;
    private Long maxitems;

    public EventLogConfig(final Node node) {
        init(node);
    }

    private void init(final Node node) {
        this.node = node;
        try {
            if (node.hasProperty(PROP_CRONEXPRESSION)) {
                this.cronexpression = node.getProperty(PROP_CRONEXPRESSION).getString();
            }
            if (node.hasProperty(PROP_KEEP_ITEMS_FOR)) {
                this.keepitemsfor = node.getProperty(PROP_KEEP_ITEMS_FOR).getLong();
            }
            if (node.hasProperty(PROP_MAXITEMS)) {
                this.maxitems = node.getProperty(PROP_MAXITEMS).getLong();
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

    public Long getKeepitemsfor() {
        return keepitemsfor;
    }

    public void setKeepitemsfor(final Long keepitemsfor) {
        this.keepitemsfor = keepitemsfor;
    }

    public Long getMaxitems() {
        return maxitems;
    }

    public void setMaxitems(final Long maxitems) {
        this.maxitems = maxitems;
    }

    public void save() throws RepositoryException {
        if (StringUtils.isNotBlank(getCronExpression())) {
            node.setProperty(PROP_CRONEXPRESSION, getCronExpression());
        } else {
            node.getProperty(PROP_CRONEXPRESSION).remove();
        }
        node.setProperty(PROP_KEEP_ITEMS_FOR, getKeepitemsfor());
        node.setProperty(PROP_MAXITEMS, getMaxitems());
        node.getSession().save();
    }
}
