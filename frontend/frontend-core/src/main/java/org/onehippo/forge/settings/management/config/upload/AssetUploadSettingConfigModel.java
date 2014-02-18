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

import org.onehippo.forge.settings.management.config.LoadableDetachableConfigModel;

/**
 * Loadable detachable model of the image upload config.
 * Configuration can be found in the repository at path:
 * /hippo:configuration/hippo:workflows/gallery/asset-gallery/frontend:renderer
 * @author Jeroen Reijn
 */
public class AssetUploadSettingConfigModel extends LoadableDetachableConfigModel<UploadSettingsConfig> {

    public static final String ASSET_UPLOAD_CONFIG_PATH = "/hippo:configuration/hippo:workflows/gallery/asset-gallery/frontend:renderer";

    @Override
    protected UploadSettingsConfig load() {
        return new UploadSettingsConfig(getConfigNode(ASSET_UPLOAD_CONFIG_PATH));
    }
}
