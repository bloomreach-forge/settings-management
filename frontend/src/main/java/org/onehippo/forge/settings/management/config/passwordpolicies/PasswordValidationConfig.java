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

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.onehippo.forge.settings.management.config.CMSFeatureConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeroen Reijn
 */
public class PasswordValidationConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(PasswordValidationConfig.class);

    private static final String PROP_MINIMALLENGTH = "minimallength";
    private static final String PROP_NUMBEROFPREVIOUSPASSWORDS = "numberofpreviouspasswords";

    public static final String NODE_MINIMALLENGTHVALIDATOR_PATH = "minimalLengthValidator";
    public static final String NODE_NO_PREVIOUSPASSWORD_PATH = "isNoPreviousValidator";
    private static final String PASSWORD_STRENGTH = "password.strength";

    private transient Node configNode;
    private Integer requiredStrength;
    private Integer minimalLengthPassword;
    private Integer numberofpreviouspasswords;


    public PasswordValidationConfig(final Node configNode) {
        init(configNode);
    }

    private void init(final Node configNode) {
        this.configNode = configNode;
        try {
            if(configNode.hasProperty(PASSWORD_STRENGTH)) {
                this.requiredStrength = Integer.valueOf(configNode.getProperty(PASSWORD_STRENGTH).getString());
            }
            if(configNode.hasNode(NODE_MINIMALLENGTHVALIDATOR_PATH)) {
                Node minimalLengtValidatorNode = configNode.getNode(NODE_MINIMALLENGTHVALIDATOR_PATH);
                if(minimalLengtValidatorNode.hasProperty(PROP_MINIMALLENGTH)) {
                    String minimallength = minimalLengtValidatorNode.getProperty(PROP_MINIMALLENGTH).getString();
                    this.minimalLengthPassword = Integer.valueOf(minimallength);
                } else {
                    //default
                    this.minimalLengthPassword = 4;
                }
            }
            if(configNode.hasNode(NODE_NO_PREVIOUSPASSWORD_PATH)) {
                Node noPreviewPasswordValidatorNode = configNode.getNode(NODE_NO_PREVIOUSPASSWORD_PATH);
                if(noPreviewPasswordValidatorNode.hasProperty(PROP_NUMBEROFPREVIOUSPASSWORDS)) {
                    String previousPasswords = noPreviewPasswordValidatorNode.getProperty(PROP_NUMBEROFPREVIOUSPASSWORDS).getString();
                    this.numberofpreviouspasswords = Integer.valueOf(previousPasswords);
                } else {
                    //default
                    this.numberofpreviouspasswords = 5;
                }
            }
        } catch (RepositoryException e) {
            log.warn("WARN: {}",e);
        }
    }

    public int getRequiredStrength() {
        return requiredStrength;
    }

    public void setRequiredStrength(final int requiredStrength) {
        this.requiredStrength = requiredStrength;
    }

    public int getMinimalLengthPassword() {
        return minimalLengthPassword;
    }

    public void setMinimalLengthPassword(final int minimalLengthPassword) {
        this.minimalLengthPassword = minimalLengthPassword;
    }

    public int getNumberofpreviouspasswords() {
        return numberofpreviouspasswords;
    }

    public void setNumberofpreviouspasswords(final int numberofpreviouspasswords) {
        this.numberofpreviouspasswords = numberofpreviouspasswords;
    }

    @Override
    public void save() throws RepositoryException {
        if(numberofpreviouspasswords!=null) {
            configNode.getNode(NODE_NO_PREVIOUSPASSWORD_PATH).setProperty(PROP_NUMBEROFPREVIOUSPASSWORDS,numberofpreviouspasswords);
        }
        if(minimalLengthPassword!=null) {
            configNode.getNode(NODE_MINIMALLENGTHVALIDATOR_PATH).setProperty(PROP_MINIMALLENGTH,minimalLengthPassword);
        }
        if(requiredStrength!=null) {
            configNode.setProperty(PASSWORD_STRENGTH,requiredStrength);
        }
    }
}
