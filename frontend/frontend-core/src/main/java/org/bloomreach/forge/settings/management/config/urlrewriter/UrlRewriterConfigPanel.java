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

import javax.jcr.RepositoryException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.ClassResourceModel;
import org.bloomreach.forge.settings.management.FeatureConfigPanel;

public class UrlRewriterConfigPanel extends FeatureConfigPanel {

    private final Model<ArrayList<String>> copyOfSkippedPrefixesModel;
    private UrlRewriterConfigModel urlRewriterConfigModel;

    public UrlRewriterConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        urlRewriterConfigModel = new UrlRewriterConfigModel();

        final RadioGroup skipPostRadioGroup =
                new RadioGroup("urlrewriter-skip-post", new PropertyModel(urlRewriterConfigModel, "skipPost"));
        skipPostRadioGroup.add(new Radio("skip-post-disabled", new Model(Boolean.FALSE)));
        skipPostRadioGroup.add(new Radio("skip-post-enabled", new Model(Boolean.TRUE)));
        add(skipPostRadioGroup);

        final RadioGroup ignoreContextPathRadioGroup =
                new RadioGroup("urlrewriter-ignore-context-path", new PropertyModel(urlRewriterConfigModel, "ignoreContextPath"));
        ignoreContextPathRadioGroup.add(new Radio("ignore-context-path-disabled", new Model(Boolean.FALSE)));
        ignoreContextPathRadioGroup.add(new Radio("ignore-context-path-enabled", new Model(Boolean.TRUE)));
        add(ignoreContextPathRadioGroup);

        final RadioGroup useQueryStringRadioGroup =
                new RadioGroup("urlrewriter-use-query-string", new PropertyModel(urlRewriterConfigModel, "useQueryString"));

        useQueryStringRadioGroup.add(new Radio("use-query-string-disabled", new Model(Boolean.FALSE)));
        useQueryStringRadioGroup.add(new Radio("use-query-string-enabled", new Model(Boolean.TRUE)));
        add(useQueryStringRadioGroup);

        ArrayList<String> copy = new ArrayList<>();
        copy.addAll(urlRewriterConfigModel.getObject().getSkippedPrefixes());
        copyOfSkippedPrefixesModel = new Model(copy);
        final WebMarkupContainer skippedPrefixes = createSkippedPrefixes(copyOfSkippedPrefixesModel);
        add(skippedPrefixes);

        final Label notInstalledMessage = new Label("not-installed", new ClassResourceModel("not-installed", UrlRewriterConfigPanel.class));
        notInstalledMessage.setVisible(false);
        add(notInstalledMessage);

        if (!urlRewriterConfigModel.getObject().hasConfiguration()) {
            notInstalledMessage.setVisible(true);
            skipPostRadioGroup.setEnabled(false);
            ignoreContextPathRadioGroup.setEnabled(false);
            useQueryStringRadioGroup.setEnabled(false);
            skippedPrefixes.setEnabled(false);
        }
    }

    private WebMarkupContainer createSkippedPrefixes(Model copyOfSkippedPrefixesModel) {

        final WebMarkupContainer listContainer = new WebMarkupContainer("skippedPrefixesContainer");
        //generate a markup-id so the contents can be updated through an AJAX call
        listContainer.setOutputMarkupId(true);
        ListView<String> skippedPrefixes = new ListView<String>("urlrewriter-skipped-prefixes", copyOfSkippedPrefixesModel) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void populateItem(final ListItem<String> item) {
                RequiredTextField prefixField = new RequiredTextField("prefix", item.getModel());
                prefixField.setOutputMarkupId(true);
                prefixField.add(new OnChangeAjaxBehavior() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                    }
                });
                item.add(prefixField);

                AjaxSubmitLink remove = new AjaxSubmitLink("remove") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form form) {
                        int index = item.getIndex();
                        getModelObject().remove(index);
                        target.add(listContainer);
                    }
                };
                remove.setDefaultFormProcessing(false);
                item.add(remove);
            }
        };

        AjaxLink addPrefix = new AjaxLink("add") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                skippedPrefixes.getModelObject().add("");
                target.add(listContainer);
                target.focusComponent(this);
            }
        };

        skippedPrefixes.setOutputMarkupId(true);
        listContainer.add(skippedPrefixes);
        listContainer.add(addPrefix);
        return listContainer;
    }

    public void save() {
        UrlRewriterConfig userManagementConfig = urlRewriterConfigModel.getObject();
        userManagementConfig.setSkippedPrefixes(copyOfSkippedPrefixesModel.getObject());

        try {
            userManagementConfig.save();
        } catch (RepositoryException e) {
            error("An error occurred while trying to save event log configuration: " + e);
        }
    }

    public void cancel() {
        // do nothing.
    }
}
