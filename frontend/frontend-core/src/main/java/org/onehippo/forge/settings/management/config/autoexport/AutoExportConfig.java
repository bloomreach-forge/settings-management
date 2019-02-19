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

package org.onehippo.forge.settings.management.config.autoexport;

import java.util.ArrayList;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.onehippo.forge.settings.management.config.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class AutoExportConfig {

    private final static Logger log = LoggerFactory.getLogger(AutoExportConfig.class);

    public static final String PROP_AUTOEXPORT_ENABLED = "autoexport:enabled";
    public static final String PROP_AUTOEXPORT_EXCLUDED_PATHS = "autoexport:excluded";
    public static final String PROP_AUTOEXPORT_FILTEREDUUIDPATHS = "autoexport:filteruuidpaths";
    public static final String PROP_AUTOEXPORT_MODULES = "autoexport:modules";
    public static final String PROP_RELOAD_ON_STARTUP_PATHS = "autoexport:reloadonstartuppaths";

    private transient Node node;

    private Boolean enabled;
    private ArrayList<String> excluded;
    private String[] filteruuidpaths;
    private ArrayList<String> modules;
    private ArrayList<String> reloadonstartuppaths;

    public AutoExportConfig(final Node node) {
        init(node);
    }

    private void init(final Node node) {
        this.node = node;
        try {
            if (node.hasProperty(PROP_AUTOEXPORT_ENABLED)) {
                this.enabled = node.getProperty(PROP_AUTOEXPORT_ENABLED).getBoolean();
            }
            if (node.hasProperty(PROP_AUTOEXPORT_EXCLUDED_PATHS)) {
                Value[] values = node.getProperty(PROP_AUTOEXPORT_EXCLUDED_PATHS).getValues();
                this.excluded = ConfigUtil.getListOfStringsFromValueArray(values);
            }
            if (node.hasProperty(PROP_AUTOEXPORT_MODULES)) {
                Value[] values = node.getProperty(PROP_AUTOEXPORT_MODULES).getValues();
                this.modules = ConfigUtil.getListOfStringsFromValueArray(values);
            }
            if (node.hasProperty(PROP_RELOAD_ON_STARTUP_PATHS)) {
                Value[] values = node.getProperty(PROP_RELOAD_ON_STARTUP_PATHS).getValues();
                this.reloadonstartuppaths = ConfigUtil.getListOfStringsFromValueArray(values);
            }
        } catch (RepositoryException e) {
            log.error("Error: {}", e);
        }
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    public ArrayList<String> getExcluded() {
        return excluded;
    }

    public void setExcluded(final ArrayList<String> excluded) {
        this.excluded = excluded;
    }

    public String[] getFilteruuidpaths() {
        return filteruuidpaths;
    }

    public void setFilteruuidpaths(final String[] filteruuidpaths) {
        this.filteruuidpaths = filteruuidpaths;
    }

    public ArrayList<String> getModules() {
        return modules;
    }

    public void setModules(final ArrayList<String> modules) {
        this.modules = modules;
    }

    public ArrayList<String> getReloadonstartuppaths() {
        return reloadonstartuppaths;
    }

    public void setReloadonstartuppaths(final ArrayList<String> reloadonstartuppaths) {
        this.reloadonstartuppaths = reloadonstartuppaths;
    }

    public void save() {
        try {
            if (this.modules != null) {
                String[] currentModules = this.modules.toArray(new String[this.modules.size()]);
                node.setProperty(PROP_AUTOEXPORT_MODULES, currentModules);
            }
            if (this.excluded != null) {
                String[] excludedPaths = this.excluded.toArray(new String[this.excluded.size()]);
                node.setProperty(PROP_AUTOEXPORT_EXCLUDED_PATHS, excludedPaths);
            }
            if (this.reloadonstartuppaths != null) {
                String[] reloadOnStartupPaths = this.reloadonstartuppaths.toArray(new String[this.reloadonstartuppaths.size()]);
                node.setProperty(PROP_RELOAD_ON_STARTUP_PATHS, reloadOnStartupPaths);
            }
            node.setProperty(PROP_AUTOEXPORT_ENABLED, this.enabled);
            node.getSession().save();
        } catch (RepositoryException e) {
            log.error("An error occurred while trying to store auto export configuration: {}", e);
        }
    }
}
