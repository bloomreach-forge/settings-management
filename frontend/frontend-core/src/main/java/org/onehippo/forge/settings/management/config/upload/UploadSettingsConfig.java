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
package org.onehippo.forge.settings.management.config.upload;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class UploadSettingsConfig implements CMSFeatureConfig {

    public static final String FILEUPLOAD_FLASH_ENABLED_SETTING = "fileupload.flashEnabled";
    public static final String FILEUPLOAD_MAX_ITEMS_SETTING = "fileupload.maxItems";

    private final static Logger logger = LoggerFactory.getLogger(UploadSettingsConfig.class);

    private int maxNumberOfFiles = 1;
    private boolean flashUploadEnabled = true;
    private transient Node node;

    public UploadSettingsConfig(final Node node) {
        init(node);
    }

    private void init(final Node node) {
        try {
            this.node = node;
            if (node.hasProperty(FILEUPLOAD_FLASH_ENABLED_SETTING)) {
                this.flashUploadEnabled = node.getProperty(FILEUPLOAD_FLASH_ENABLED_SETTING).getBoolean();
            }
            if (node.hasProperty(FILEUPLOAD_MAX_ITEMS_SETTING)) {
                this.maxNumberOfFiles = Long.valueOf(node.getProperty(FILEUPLOAD_MAX_ITEMS_SETTING).getLong()).intValue();
            }
        } catch (RepositoryException e) {
            logger.error("Error while trying to fetch the upload settings: {}",e);
        }
    }

    public Integer getMaxNumberOfFiles() {
        return maxNumberOfFiles;
    }

    public void setMaxNumberOfFiles(final int maxNumberOfFiles) {
        this.maxNumberOfFiles = maxNumberOfFiles;
    }

    public Boolean isFlashUploadEnabled() {
        return flashUploadEnabled;
    }

    public void setFlashUploadEnabled(final boolean flashUploadEnabled) {
        this.flashUploadEnabled = flashUploadEnabled;
    }

    @Override
    public void save() throws RepositoryException {
        node.setProperty(FILEUPLOAD_FLASH_ENABLED_SETTING,this.flashUploadEnabled);
        node.setProperty(FILEUPLOAD_MAX_ITEMS_SETTING,this.maxNumberOfFiles);
        node.getSession().save();
    }
}
