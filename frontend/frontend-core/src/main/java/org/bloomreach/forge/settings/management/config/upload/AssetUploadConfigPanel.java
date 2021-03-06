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

package org.bloomreach.forge.settings.management.config.upload;

import javax.jcr.RepositoryException;

import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.bloomreach.forge.settings.management.FeatureConfigPanel;
import org.bloomreach.forge.settings.management.config.CMSFeatureConfig;

/**
 * Panel for configuring the asset gallery upload settings.
 */
public class AssetUploadConfigPanel extends FeatureConfigPanel {
    private AssetUploadSettingConfigModel assetUploadSettingConfigModel = new AssetUploadSettingConfigModel();

    public AssetUploadConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        final UploadSettingsConfig uploadSettingsConfig = assetUploadSettingConfigModel.getObject();

        final RequiredTextField<String> maxFilesizeField = new RequiredTextField<String>("assetupload-max-items", new PropertyModel(uploadSettingsConfig, "maxNumberOfFiles"));
        add(maxFilesizeField);
    }

    public void save() {
        CMSFeatureConfig config = assetUploadSettingConfigModel.getConfig();

        try {
            config.save();
        } catch (RepositoryException e) {
            error("An error occurred while trying to save event log configuration: " + e);
        }
    }

    public void cancel() {
        // do nothing.
    }
}
