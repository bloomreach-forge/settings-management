/*
 * Copyright 2016-2019 BloomReach Inc. (http://www.bloomreach.com)
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
package org.bloomreach.forge.settings.management.config.urlrewriter;

import java.util.ArrayList;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.apache.commons.lang.StringUtils;
import org.bloomreach.forge.settings.management.config.CMSFeatureConfig;
import org.bloomreach.forge.settings.management.config.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlRewriterConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(UrlRewriterConfig.class);

    public static final String CONFIGURATION_PATH = "/hippo:configuration/hippo:modules/urlrewriter/hippo:moduleconfig";

    private static final String SKIP_POST = "urlrewriter:skippost";
    private static final String IGNORE_CONTEXT_PATH = "urlrewriter:ignorecontextpath";
    private static final String USE_QUERY_STRING = "urlrewriter:usequerystring";
    private static final String SKIPPED_PREFIXES = "urlrewriter:skippedprefixes";
    private static final String DISALLOWED_DUPLICATE_HEADERS = "urlrewriter:disallowedduplicateheaders";

    private final transient Node node;

    private Boolean skipPost = true;
    private Boolean ignoreContextPath = true;
    private Boolean useQueryString = false;
    private ArrayList<String> skippedPrefixes = new ArrayList<>(0);
    private ArrayList<String> disallowedDuplicateHeaders = new ArrayList<>(0);

    public Boolean hasConfiguration() {
        return node != null;
    }

    public Boolean getSkipPost() {
        return skipPost;
    }

    public void setSkipPost(final Boolean skipPost) {
        this.skipPost = skipPost;
    }

    public Boolean getUseQueryString() {
        return useQueryString;
    }

    public void setUseQueryString(final Boolean useQueryString) {
        this.useQueryString = useQueryString;
    }

    public Boolean getIgnoreContextPath() {
        return ignoreContextPath;
    }

    public void setIgnoreContextPath(final Boolean ignoreContextPath) {
        this.ignoreContextPath = ignoreContextPath;
    }

    public ArrayList<String> getSkippedPrefixes() {
        return skippedPrefixes;
    }

    public void setSkippedPrefixes(final ArrayList<String> skippedPrefixes) {
        this.skippedPrefixes = skippedPrefixes;
    }

    public void removeSkippedPrefix(final String prefix){
        skippedPrefixes.remove(prefix);
    }

    public void addPrefix(){
        skippedPrefixes.add(StringUtils.EMPTY);
    }

    public ArrayList<String> getDisallowedDuplicateHeaders() {
        return disallowedDuplicateHeaders;
    }

    public void setDisallowedDuplicateHeaders(final ArrayList<String> disallowedDuplicateHeaders) {
        this.disallowedDuplicateHeaders = disallowedDuplicateHeaders;
    }

    public UrlRewriterConfig(final Node configNode) {

        this.node = configNode;
        if (this.node != null) {
            try {
                if (node.hasProperty(SKIP_POST)) {
                    this.skipPost = node.getProperty(SKIP_POST).getBoolean();
                }
                if (node.hasProperty(USE_QUERY_STRING)) {
                    this.useQueryString = node.getProperty(USE_QUERY_STRING).getBoolean();
                }
                if (node.hasProperty(IGNORE_CONTEXT_PATH)) {
                    this.ignoreContextPath = node.getProperty(IGNORE_CONTEXT_PATH).getBoolean();
                }
                if (node.hasProperty(SKIPPED_PREFIXES)) {
                    Value[] values = node.getProperty(SKIPPED_PREFIXES).getValues();
                    this.skippedPrefixes = ConfigUtil.getListOfStringsFromValueArray(values);
                }
                if (node.hasProperty(DISALLOWED_DUPLICATE_HEADERS)) {
                    Value[] values = node.getProperty(DISALLOWED_DUPLICATE_HEADERS).getValues();
                    this.disallowedDuplicateHeaders = ConfigUtil.getListOfStringsFromValueArray(values);
                }
            } catch (RepositoryException e) {
                log.error("Error: {}", e);
            }
        }
    }

    @Override
    public void save() throws RepositoryException {
        node.setProperty(SKIP_POST, skipPost);
        node.setProperty(USE_QUERY_STRING, useQueryString);
        node.setProperty(IGNORE_CONTEXT_PATH, ignoreContextPath);
        if (skippedPrefixes != null) {
            String[] prefixes = skippedPrefixes.toArray(new String[skippedPrefixes.size()]);
            node.setProperty(SKIPPED_PREFIXES, prefixes);
        }
        if (disallowedDuplicateHeaders != null) {
            String[] disallowedDuplicates = disallowedDuplicateHeaders.toArray(new String[disallowedDuplicateHeaders.size()]);
            node.setProperty(DISALLOWED_DUPLICATE_HEADERS, disallowedDuplicates);
        }
        node.getSession().save();
    }
}
