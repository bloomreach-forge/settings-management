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

import org.bloomreach.forge.settings.management.config.LoadableDetachableConfigModel;

/**
 * Loadable detachable model of the image upload config.
 * Configuration can be found in the repository at path:
 * hippo:configuration/hippo:frontend/cms/cms-services/imageValidationService
 */
public class ImageValidationServiceConfigModel extends LoadableDetachableConfigModel<ImageValidationServiceConfig> {
    public static final String IMAGE_VALIDATION_CONFIG_PATH = "/hippo:configuration/hippo:frontend/cms/cms-services/imageValidationService";

    @Override
    protected ImageValidationServiceConfig load() {
        return new ImageValidationServiceConfig(getConfigNode(IMAGE_VALIDATION_CONFIG_PATH));
    }
}
