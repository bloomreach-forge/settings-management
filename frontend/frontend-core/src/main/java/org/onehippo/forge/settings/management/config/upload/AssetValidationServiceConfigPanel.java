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

package org.onehippo.forge.settings.management.config.upload;

import javax.jcr.RepositoryException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.settings.management.FeatureConfigPanel;
import org.onehippo.forge.settings.management.config.CMSFeatureConfig;

/**
 * Panel for configuring the image validation service.
 */
public class AssetValidationServiceConfigPanel extends FeatureConfigPanel {

    private AssetValidationServiceConfigModel assetValidationServiceConfigModel;

    public AssetValidationServiceConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        assetValidationServiceConfigModel = new AssetValidationServiceConfigModel();
        final AssetValidationServiceConfig assetValidationServiceConfig = assetValidationServiceConfigModel.getObject();

        addAllowedExtensionsField(assetValidationServiceConfig);
        final RequiredTextField<String> maxFilesizeField = new RequiredTextField<String>("assetvalidation-max-file-size", new PropertyModel(assetValidationServiceConfig, "maxFileSize"));
        add(maxFilesizeField);
    }

    public void save() {
        CMSFeatureConfig config = assetValidationServiceConfigModel.getConfig();

        try {
            config.save();
        } catch (RepositoryException e) {
            error("An error occurred while trying to save event log configuration: " + e);
        }
    }

    private void addAllowedExtensionsField(AssetValidationServiceConfig assetValidationServiceConfig) {

        final WebMarkupContainer listContainer = new WebMarkupContainer("allowedExtensionsView");
        //generate a markup-id so the contents can be updated through an AJAX call
        listContainer.setOutputMarkupId(true);

        // Don't do this in a listview
        final ListView<String> allowedExtensionsView;
        allowedExtensionsView = new ListView<String>("assetvalidation-allowedExtensions", new PropertyModel(assetValidationServiceConfig, "allowedExtensions")) {
            @Override
            protected void populateItem(final ListItem<String> item) {
                RequiredTextField extensionField = new RequiredTextField("asset-extension", item.getModel());
                extensionField.setOutputMarkupId(true);
                extensionField.add(new OnChangeAjaxBehavior() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                        System.out.println(item.getModelObject());
                    }
                });
                item.add(extensionField);

                AjaxSubmitLink remove = new AjaxSubmitLink("remove") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form form) {
                        getModelObject().remove(item.getModelObject());
                        target.add(listContainer);
                    }
                };
                remove.setDefaultFormProcessing(false);
                item.add(remove);
            }
        };

        AjaxLink addExtension = new AjaxLink("add-extension") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                allowedExtensionsView.getModelObject().add("");
                target.add(listContainer);
                target.focusComponent(this);
            }
        };

        allowedExtensionsView.setReuseItems(true);

        listContainer.add(allowedExtensionsView);
        listContainer.add(addExtension);
        add(listContainer);
    }

    public void cancel() {
        // do nothing.
    }
}
