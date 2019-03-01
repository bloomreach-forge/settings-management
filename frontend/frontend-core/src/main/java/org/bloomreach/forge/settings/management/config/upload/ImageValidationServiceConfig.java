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

package org.bloomreach.forge.settings.management.config.upload;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.hippoecm.frontend.plugins.yui.upload.validation.ImageUploadValidationService;
import org.bloomreach.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ImageValidationServiceConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(ImageValidationServiceConfig.class);

    public static final String PROP_MAX_HEIGHT = "max.height";
    public static final String PROP_MAX_WIDTH = "max.height";

    private transient Node node;
    private List<String> allowedExtensions;
    private String maxFileSize = "";
    private long maxWidth;
    private long maxHeight;

    public ImageValidationServiceConfig(final Node node) {
        init(node);
    }

    private void init(final Node node) {
        this.node = node;
        try {
            if (node.hasProperty(ImageUploadValidationService.EXTENSIONS_ALLOWED)) {
                allowedExtensions = getArrayListOfValueArray(node.getProperty(ImageUploadValidationService.EXTENSIONS_ALLOWED).getValues());
            }
            if (node.hasProperty(ImageUploadValidationService.MAX_FILE_SIZE)) {
                maxFileSize = node.getProperty(ImageUploadValidationService.MAX_FILE_SIZE).getString();
            }
            if( node.hasProperty(PROP_MAX_HEIGHT)) {
                maxHeight = node.getProperty(PROP_MAX_HEIGHT).getLong();
            }
            if( node.hasProperty(PROP_MAX_WIDTH)) {
                maxWidth = node.getProperty(PROP_MAX_WIDTH).getLong();
            }

        } catch (RepositoryException e) {
            log.error("Error: {}", e);
        }
    }

    private ArrayList<String> getArrayListOfValueArray(Value[] values) {
        ArrayList<String> newValues = new ArrayList<String>(values.length);
        for (int i = 0; i < values.length; i++) {
            try {
                newValues.add(values[i].getString());
            } catch (RepositoryException e) {
                log.error("Error: {}", e);
            }
        }
        return newValues;
    }

    public List<String> getAllowedExtensions() {
        return allowedExtensions;
    }

    public void setAllowedExtensions(final List<String> allowedExtensions) {
        this.allowedExtensions = allowedExtensions;
    }

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(final String maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Long getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(final Long maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Long getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(final Long maxHeight) {
        this.maxHeight = maxHeight;
    }

    public void save() throws RepositoryException {
        if (this.allowedExtensions != null) {
            String[] selectedExtensions = this.allowedExtensions.toArray(new String[this.allowedExtensions.size()]);
            node.setProperty(ImageUploadValidationService.EXTENSIONS_ALLOWED, selectedExtensions);
        }
        if (this.maxFileSize !=null) {
            node.setProperty(ImageUploadValidationService.MAX_FILE_SIZE,maxFileSize);
        }
        node.setProperty(PROP_MAX_HEIGHT,maxHeight);
        node.setProperty(PROP_MAX_WIDTH,maxWidth);
        node.getSession().save();
    }
}
