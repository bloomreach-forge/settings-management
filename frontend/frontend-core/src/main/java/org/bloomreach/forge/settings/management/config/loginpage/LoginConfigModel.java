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

package org.bloomreach.forge.settings.management.config.loginpage;

import org.bloomreach.forge.settings.management.config.LoadableDetachableConfigModel;

/**
 * @author Jeroen Reijn
 */
public class LoginConfigModel extends LoadableDetachableConfigModel<LoginConfig> {
    private static final long serialVersionUID = 1L;
    public static final String LOGIN_PAGE_CONFIG_PATH = "/hippo:configuration/hippo:frontend/login/login/loginPage";

    @Override
    protected LoginConfig load() {
        return new LoginConfig(getConfigNode(LOGIN_PAGE_CONFIG_PATH));
    }
}
