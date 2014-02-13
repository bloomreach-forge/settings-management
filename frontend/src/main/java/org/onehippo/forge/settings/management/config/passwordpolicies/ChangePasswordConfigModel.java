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

package org.onehippo.forge.settings.management.config.passwordpolicies;

import org.onehippo.forge.settings.management.config.LoadableDetachableConfigModel;

/**
 * @author Jeroen Reijn
 */
public class ChangePasswordConfigModel extends LoadableDetachableConfigModel<ChangePasswordConfig> {

    public static final String CHANGE_PASSWORD_CONFIG_PATH = "hippo:configuration/hippo:frontend/cms/cms-dashshortcuts/changePasswordShortcut";

    @Override
    protected ChangePasswordConfig load() {
        return new ChangePasswordConfig(getConfigNode(CHANGE_PASSWORD_CONFIG_PATH));
    }
}
