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
package org.onehippo.forge.settings.management.config;

import java.util.ArrayList;

import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class ConfigUtil {

    private final static Logger logger = LoggerFactory.getLogger(ConfigUtil.class);

    public static ArrayList<String> getListOfStringsFromValueArray(Value[] values) {
        ArrayList<String> newValues = new ArrayList<String>(values.length);

        for (int i = 0; i < values.length; i++) {
            try {
                newValues.add(values[i].getString());
            } catch (RepositoryException e) {
                logger.error("error: {}", e);
            }
        }
        return newValues;
    }

}
