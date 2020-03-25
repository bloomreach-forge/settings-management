/*
 * Copyright 2020 Bloomreach Inc. (http://www.bloomreach.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bloomreach.forge.settings.management.config.crispapi;

import java.io.Serializable;

public class CrispResourceSpace implements Serializable, Comparable<CrispResourceSpace> {

    private String backendType;
    private String resourceSpaceName;

    public CrispResourceSpace() {
    }

    public CrispResourceSpace(final String backendType, final String resourceSpaceName) {
        this.backendType = backendType;
        this.resourceSpaceName = resourceSpaceName;
    }

    public String getBackendType() {
        return backendType;
    }

    public void setBackendType(String backendType) {
        this.backendType = backendType;
    }

    public String getResourceSpaceName() {
        return resourceSpaceName;
    }

    public void setResourceSpaceName(String resourceSpaceName) {
        this.resourceSpaceName = resourceSpaceName;
    }

    @Override
    public int compareTo(CrispResourceSpace that) {
        if (this.resourceSpaceName == null && that.resourceSpaceName == null) {
            return 0;
        } else if (this.resourceSpaceName == null && that.resourceSpaceName != null) {
            return -1;
        } else if (this.resourceSpaceName != null && that.resourceSpaceName == null) {
            return 1;
        } else {
            return this.resourceSpaceName.compareTo(that.resourceSpaceName);
        }
    }

}
