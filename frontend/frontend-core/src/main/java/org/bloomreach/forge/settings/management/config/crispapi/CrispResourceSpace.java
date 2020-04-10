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
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class CrispResourceSpace implements Serializable, Cloneable {

    private String resourceSpaceName;
    private String backendTypeName;
    private List<CrispResourceSpaceProperty> properties = new LinkedList<>();
    private String beansDefinition;

    public CrispResourceSpace() {
    }

    public CrispResourceSpace(final String resourceSpaceName, final String backendTypeName) {
        this.resourceSpaceName = resourceSpaceName;
        this.backendTypeName = backendTypeName;
    }

    public String getResourceSpaceName() {
        return resourceSpaceName;
    }

    public void setResourceSpaceName(final String resourceSpaceName) {
        this.resourceSpaceName = resourceSpaceName;
    }

    public String getBackendTypeName() {
        return backendTypeName;
    }

    public void setBackendTypeName(final String backendTypeName) {
        this.backendTypeName = backendTypeName;
    }

    public List<CrispResourceSpaceProperty> getProperties() {
        return properties;
    }

    public void setProperties(final List<CrispResourceSpaceProperty> properties) {
        this.properties.clear();

        if (properties != null) {
            this.properties.addAll(properties);
        }
    }

    public String[] getPropertyNames() {
        final String[] propNames = new String[properties.size()];
        int i = 0;
        for (CrispResourceSpaceProperty prop : properties) {
            propNames[i++] = prop.getName();
        }
        return propNames;
    }

    public String[] getPropertyValues() {
        final String[] propValues = new String[properties.size()];
        int i = 0;
        for (CrispResourceSpaceProperty prop : properties) {
            propValues[i++] = prop.getValue();
        }
        return propValues;
    }

    public String getBeansDefinition() {
        return beansDefinition;
    }

    public void setBeansDefinition(String beansDefinition) {
        this.beansDefinition = beansDefinition;
    }

    public void addProperty(final CrispResourceSpaceProperty property) {
        properties.add(property);
    }

    public Set<String> getConcealedPropertyNameSet() {
        return properties.stream().filter(p -> p.isConcealed()).map(p -> p.getName()).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("resourceSpaceName", resourceSpaceName)
                .append("backendTypeName", backendTypeName)
                .append("properties", properties)
                .append("beansDefinition", beansDefinition)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CrispResourceSpace)) {
            return false;
        }

        final CrispResourceSpace that = (CrispResourceSpace) o;

        return Objects.equals(this.resourceSpaceName, that.resourceSpaceName)
                && Objects.equals(this.backendTypeName, that.backendTypeName)
                && Objects.equals(this.properties, that.properties)
                && Objects.equals(this.beansDefinition, that.beansDefinition);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(resourceSpaceName)
                .append(backendTypeName)
                .append(properties)
                .append(beansDefinition)
                .toHashCode();
    }

    @Override
    public Object clone() {
        final CrispResourceSpace clone = new CrispResourceSpace(this.resourceSpaceName, this.backendTypeName);

        if (this.properties != null && !this.properties.isEmpty()) {
            for (CrispResourceSpaceProperty prop : this.properties) {
                clone.addProperty((CrispResourceSpaceProperty) prop.clone());
            }
        }

        clone.setBeansDefinition(this.beansDefinition);

        return clone;
    }
}
