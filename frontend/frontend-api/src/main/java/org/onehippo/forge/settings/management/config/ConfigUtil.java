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
