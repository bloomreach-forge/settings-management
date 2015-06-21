/*
 * Copyright 2015 Hippo B.V. (http://www.onehippo.com)
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
package org.onehippo.forge.settings.management.config;

import java.util.NoSuchElementException;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.repository.util.JcrUtils;

public class SchedulerUtils {

    private static final String PROP_ATTR_NAMES = "hipposched:attributeNames";
    private static final String PROP_ATTR_VALUES = "hipposched:attributeValues";

    private static final String PROP_CRONEXPRESSION = "hipposched:cronExpression";
    private static final String PROP_ENABLED = "hipposched:enabled";

    private static final String NODE_TRIGGERS = "hipposched:triggers";
    private static final String TYPE_CRONTRIGGER = "hipposched:crontrigger";


    private SchedulerUtils() {}

    public static String getAttribute(final Node jobNode, final String attributeName, final String defaultValue) throws RepositoryException {
        final Value[] names = jobNode.getProperty(PROP_ATTR_NAMES).getValues();
        final Value[] values = jobNode.getProperty(PROP_ATTR_VALUES).getValues();
        for (int i = 0; i < names.length; i++) {
            if (attributeName.equals(names[i].getString())) {
                if (i > values.length-1) {
                    throw new RepositoryException("No corresponding attribute value at position " + i);
                }
                return values[i].getString();
            }
        }
        return defaultValue;
    }

    public static Long getAttributeAsLong(final Node jobNode, final String attributeName, final Long defaultValue) throws RepositoryException {
        final String attributeValue = getAttribute(jobNode, attributeName, null);
        if (attributeValue != null) {
            try {
                return Long.valueOf(attributeValue);
            } catch (NumberFormatException e) {
                throw new RepositoryException("Attribute " + attributeName + " of job "
                        + JcrUtils.getNodePathQuietly(jobNode) + " not convertible to Long");
            }
        }
        return defaultValue;
    }

    public static Boolean getAttributeAsBoolean(final Node jobNode, final String attributeName, final Boolean defaultValue) throws RepositoryException {
        final String attributeValue = getAttribute(jobNode, attributeName, null);
        if (attributeValue != null) {
            return Boolean.valueOf(attributeValue);
        }
        return defaultValue;
    }

    public static void setAttribute(final Node jobNode, final String attributeName, final Object attributeValue) throws RepositoryException {
        Value[] names = jobNode.getProperty(PROP_ATTR_NAMES).getValues();
        int position = -1;
        for (int i = 0; i < names.length; i++) {
            if (names[i].getString().equals(attributeName)) {
                position = i;
                break;
            }
        }
        final String value = attributeValue != null ? attributeValue.toString() : "";
        final ValueFactory valueFactory = jobNode.getSession().getValueFactory();
        Value[] values;
        if (position == -1) {
            Value[] newNames = new Value[names.length+1];
            System.arraycopy(names, 0, newNames, 0, names.length);
            newNames[newNames.length - 1] = valueFactory.createValue(attributeName);
            jobNode.setProperty(PROP_ATTR_NAMES, newNames);
            Value[] oldValues = jobNode.getProperty(PROP_ATTR_VALUES).getValues();
            values = new Value[names.length+1];
            System.arraycopy(oldValues, 0, values, 0, oldValues.length);
            values[values.length-1] = valueFactory.createValue(value);
        } else {
            values = jobNode.getProperty(PROP_ATTR_VALUES).getValues();
            values[position] = valueFactory.createValue(value);
        }
        jobNode.setProperty(PROP_ATTR_VALUES, values);
    }

    public static String getCronExpression(final Node jobNode) throws RepositoryException {
        try {
            return jobNode.getNode(NODE_TRIGGERS).getNodes().nextNode().getProperty(PROP_CRONEXPRESSION).getString();
        } catch (NoSuchElementException e) {
            throw new PathNotFoundException("No trigger node found");
        }
    }

    public static void setCronExpression(final Node jobNode, final String cronExpression) throws RepositoryException {
        Node triggerNode;
        try {
            triggerNode = jobNode.getNode(NODE_TRIGGERS).getNodes().nextNode();
        } catch (NoSuchElementException e) {
            triggerNode = jobNode.getNode(NODE_TRIGGERS).addNode("nightly", TYPE_CRONTRIGGER);
        }
        if (StringUtils.isBlank(cronExpression)) {
            triggerNode.remove();
        } else {
            triggerNode.setProperty(PROP_CRONEXPRESSION, cronExpression);
        }
    }

    public static boolean isEnabled(final Node jobNode) throws RepositoryException {
        return JcrUtils.getBooleanProperty(jobNode, PROP_ENABLED, true);
    }

}
