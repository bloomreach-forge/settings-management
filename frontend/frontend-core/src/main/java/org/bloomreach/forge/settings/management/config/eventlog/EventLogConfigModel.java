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

package org.bloomreach.forge.settings.management.config.eventlog;

import org.bloomreach.forge.settings.management.config.LoadableDetachableConfigModel;

/**
 * EventLogConfigModel
 */
public class EventLogConfigModel extends LoadableDetachableConfigModel<EventLogConfig> {
    public static final String EVENTLOG_CLEANUP_JOB_PATH = "/hippo:configuration/hippo:modules/scheduler/hippo:moduleconfig/system/EventLogCleanup";

    @Override
    protected EventLogConfig load() {
        return new EventLogConfig(getConfigNode(EVENTLOG_CLEANUP_JOB_PATH));
    }

}
