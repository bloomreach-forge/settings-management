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

public final class CrispApiConfigConstants {

    /**
     * CRISP API ResourceResolver configuration path.
     */
    public static final String CRISP_CONFIG_PATH = "/hippo:configuration/hippo:modules/crispregistry/hippo:moduleconfig/crisp:resourceresolvercontainer";

    /**
     * The name of the property containing all available backend template names.
     */
    public static final String PROP_CRISP_TEMPLATES = "crisp.templates";

    /**
     * The name prefix of the properties for backend specific configurations.
     */
    public static final String PROP_CRISP_TEMPLATE_PREFIX = "crisp.template.";

    /**
     * The name of the property for the class FQCNs to depend on.
     */
    public static final String PROP_CRISP_TEMPLATE_DEPENDS_FORMAT = PROP_CRISP_TEMPLATE_PREFIX + "%s.depends";

    /**
     * The name of the property for the backend specific bean defintions XML.
     */
    public static final String PROP_CRISP_TEMPLATE_BEANDEFINITION_FORMAT = PROP_CRISP_TEMPLATE_PREFIX
            + "%s.beandefinition";

    /**
     * The name of the property for the backend specific crisp property names.
     */
    public static final String PROP_CRISP_TEMPLATE_PROPNAMES_FORMAT = PROP_CRISP_TEMPLATE_PREFIX + "%s.propnames";

    /**
     * The name of the property for the backend specific crisp property values.
     */
    public static final String PROP_CRISP_TEMPLATE_PROPVALUES_FORMAT = PROP_CRISP_TEMPLATE_PREFIX + "%s.propvalues";

    /**
     * The Spring Framework &ltmeta /&gt; element's key attribute value, which may contain the hint of the backend
     * type name in the existing beans XML.
     */
    public static final String META_BACKEND_TYPE_KEY = "org.bloomreach.forge.settings.management.config.crispapi.backendType";

    /**
     * The backend type name prefix for brX Commerce Accelerator connector commerce backends.
     */
    public static final String BRX_BACKEND_TYPE_NAME_PREFIX = "brx-";

    private CrispApiConfigConstants() {
    }

}
