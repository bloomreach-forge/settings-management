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

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.hippoecm.frontend.plugins.yui.upload.validation.DefaultUploadValidationService;
import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class AssetValidationServiceConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(AssetValidationServiceConfig.class);

    private transient Node node;
    private List<String> allowedExtensions;
    private String maxFileSize = "";

    public AssetValidationServiceConfig(final Node node) {
        init(node);
    }

    private void init(final Node node) {
        this.node = node;
        try {
            if (node.hasProperty(DefaultUploadValidationService.EXTENSIONS_ALLOWED)) {
                allowedExtensions = getArrayListOfValueArray(node.getProperty(DefaultUploadValidationService.EXTENSIONS_ALLOWED).getValues());
            } else {
                allowedExtensions = new ArrayList<>();
            }
            if (node.hasProperty(DefaultUploadValidationService.MAX_FILE_SIZE)) {
                maxFileSize = node.getProperty(DefaultUploadValidationService.MAX_FILE_SIZE).getString();
            }
        } catch (RepositoryException e) {
            log.error("Error: {}", e);
        }
    }

    private List<String> getArrayListOfValueArray(Value[] values) {
        List<String> newValues = new ArrayList<String>(values.length);
        for (final Value value : values) {
            try {
                newValues.add(value.getString());
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

    public void save() throws RepositoryException {
        if (this.allowedExtensions != null) {
            String[] selectedExtensions = this.allowedExtensions.toArray(new String[this.allowedExtensions.size()]);
            node.setProperty(DefaultUploadValidationService.EXTENSIONS_ALLOWED, selectedExtensions);
        }
        if (this.maxFileSize !=null) {
            node.setProperty(DefaultUploadValidationService.MAX_FILE_SIZE, maxFileSize);
        }
        node.getSession().save();
    }
}
