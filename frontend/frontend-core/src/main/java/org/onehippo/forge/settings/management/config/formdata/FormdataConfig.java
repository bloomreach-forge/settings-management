/*
 * Copyright 2014 Hippo B.V. (http://www.onehippo.com)
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

import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.frontend.plugins.yui.upload.validation.ImageUploadValidationService;
import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.onehippo.forge.settings.management.config.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class FormdataConfig implements CMSFeatureConfig {

    private final static Logger logger = LoggerFactory.getLogger(FormdataConfig.class);

    public static final String PROP_CRONEXPRESSION = "cronexpression";
    public static final String PROP_MINUTES_TO_LIVE = "minutestolive";
    public static final String PROP_EXCLUDE_PATHS = "excludepaths";

    private transient Node node;

    private String cronexpression;
    private Long minutesToLive;
    private List<String> excludepaths;
    private boolean dataAvailable = true;

    public FormdataConfig(final Node node) {
        if(node != null) {
            init(node);
        } else {
            dataAvailable = false;
        }
    }

    private void init(final Node node) {
        this.node = node;
        try {
            if (node.hasProperty(PROP_CRONEXPRESSION)) {
                this.cronexpression = node.getProperty(PROP_CRONEXPRESSION).getString();
            }
            if (node.hasProperty(PROP_MINUTES_TO_LIVE)) {
                this.minutesToLive = node.getProperty(PROP_MINUTES_TO_LIVE).getLong();
            }
            if (node.hasProperty(PROP_EXCLUDE_PATHS)) {
                final Value[] values = node.getProperty(PROP_EXCLUDE_PATHS).getValues();
                excludepaths = ConfigUtil.getListOfStringsFromValueArray(values);
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

    public void save() throws RepositoryException {
        if (this.excludepaths != null) {
            String[] selectedPaths = this.excludepaths.toArray(new String[this.excludepaths.size()]);
            node.setProperty(PROP_EXCLUDE_PATHS, selectedPaths);
        }
        if (StringUtils.isNotBlank(getCronExpression())) {
            node.setProperty(PROP_CRONEXPRESSION, getCronExpression());
        } else {
            node.getProperty(PROP_CRONEXPRESSION).remove();
        }
        node.setProperty(PROP_MINUTES_TO_LIVE, getMinutesToLive());
        node.getSession().save();
    }

    public boolean isDataAvailable() {
        return this.dataAvailable;
    }
}
