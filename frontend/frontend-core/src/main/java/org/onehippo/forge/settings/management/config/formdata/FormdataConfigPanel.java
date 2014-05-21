/*
 * Copyright 2014 Hippo B.V. (http://www.onehippo.com)
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

package org.onehippo.forge.settings.management.config.formdata;

import javax.jcr.RepositoryException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.settings.management.FeatureConfigPanel;
import org.onehippo.forge.settings.management.config.CMSFeatureConfig;

/**
 */
public class FormdataConfigPanel extends FeatureConfigPanel {

    private FormdataConfigModel formdataConfigModel = new FormdataConfigModel();
    private boolean isVisble;

    public FormdataConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        final FormdataConfig formdata = formdataConfigModel.getObject();
        isVisble = formdata.isDataAvailable();

        add(new TextField("cronexpression", new PropertyModel(formdata, "cronexpression")));
        add(new TextField("minutestolive", new PropertyModel(formdata, "minutesToLive")));
        addExclusionPaths(formdata);
    }

    private void addExclusionPaths(FormdataConfig formdataConfig) {
        final WebMarkupContainer listContainer = new WebMarkupContainer("excludedPathsView");
        //generate a markup-id so the contents can be updated through an AJAX call
        listContainer.setOutputMarkupId(true);

        // Don't do this in a listview
        final ListView<String> excludedPathsView;
        excludedPathsView = new ListView<String>("formdata-excludedPaths", new PropertyModel(formdataConfig, "excludepaths")) {
            @Override
            protected void populateItem(final ListItem<String> item) {
                RequiredTextField extensionField = new RequiredTextField("formdata-path", item.getModel());
                extensionField.setOutputMarkupId(true);
                item.add(extensionField);

                AjaxSubmitLink remove = new AjaxSubmitLink("remove") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form form) {
                        getModelObject().remove(item.getModelObject());
                        target.addComponent(listContainer);
                    }
                };
                remove.setDefaultFormProcessing(false);
                item.add(remove);
            }
        };

        AjaxLink addExtension = new AjaxLink("add-path") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                excludedPathsView.getModelObject().add("");
                target.addComponent(listContainer);
                target.focusComponent(this);
            }
        };

        excludedPathsView.setReuseItems(true);

        listContainer.add(excludedPathsView);
        listContainer.add(addExtension);
        add(listContainer);
    }

    public void save() {
        CMSFeatureConfig config = formdataConfigModel.getConfig();
        try {
            config.save();
        } catch (RepositoryException e) {
            error("An error occurred while trying to save form data configuration: " + e);
        }
    }

    public void cancel() {
        // do nothing.
    }

    @Override
    public boolean isVisible() {
        return isVisble;
    }

}
