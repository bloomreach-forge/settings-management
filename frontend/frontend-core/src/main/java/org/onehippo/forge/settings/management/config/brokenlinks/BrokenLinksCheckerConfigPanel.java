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

package org.onehippo.forge.settings.management.config.brokenlinks;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.wicket.Component;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.PluginRequestTarget;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.DialogAction;
import org.hippoecm.frontend.dialog.IDialogFactory;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.editor.plugins.linkpicker.LinkPickerDialog;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.settings.management.FeatureConfigPanel;
import org.onehippo.forge.settings.management.SettingsPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokenLinksCheckerConfigPanel extends FeatureConfigPanel {

    private final static Logger log = LoggerFactory.getLogger(BrokenLinksCheckerConfigPanel.class);
    private BrokenLinksCheckerConfigModel brokenLinksCheckerConfigModel;
    private String checkerContentUUID;
    private String checkerStartPath;

    public BrokenLinksCheckerConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        brokenLinksCheckerConfigModel = new BrokenLinksCheckerConfigModel();

        BrokenlinksCheckerConfig brokenlinksCheckerConfig = brokenLinksCheckerConfigModel.getObject();
        checkerContentUUID = brokenlinksCheckerConfig.getStartPathUUID();
        checkerStartPath =  brokenlinksCheckerConfig.getStartPath();

        TextField brokenlinksCheckConnectionTimeoutField = new TextField("linkscheck-connectionTimeout", new PropertyModel(brokenLinksCheckerConfigModel,"connectionTimeout"));
        TextField brokenlinksCheckSocketTimeoutField = new TextField("linkscheck-socketTimeout", new PropertyModel(brokenLinksCheckerConfigModel,"socketTimeout"));
        TextField brokenlinksCheckNrOfThreads = new TextField("linkscheck-nrOfThreads", new PropertyModel(brokenLinksCheckerConfigModel,"nrOfThreads"));

        final TextField brokenlinksCheckStartPath = new TextField("linkscheck-startPath", new PropertyModel(this, "checkerStartPath"));
        final Image locationPickLink = new Image("linkcheck-startPath-location-search", new ResourceReference(BrokenLinksCheckerConfigPanel.class, "folder-choose.png")) {
            @Override
            public boolean isVisible() {
                return true;
            }
        };
        brokenlinksCheckStartPath.setOutputMarkupId(true);
        brokenlinksCheckStartPath.add(createSimpleAjaxChangeBehavior(brokenlinksCheckStartPath));

        final PropertyModel<String> contentUUID = new PropertyModel<String>(this, "checkerContentUUID");
        final IDialogFactory dialogFactory = new IDialogFactory() {
            private static final long serialVersionUID = 1L;
            @Override
            public AbstractDialog<String> createDialog() {

                return new LinkPickerDialog(getContext(), getConfig(), contentUUID) {

                    @Override
                    public void render(PluginRequestTarget target) {
                        target.addComponent(brokenlinksCheckStartPath);
                        super.render(target);
                    }

                    @Override
                    protected IModel<Node> getFolderModel() {
                        return super.getFolderModel();
                    }

                    @Override
                    protected void saveNode(Node node) {
                        try {
                            getModel().setObject(node.getIdentifier());
                            checkerStartPath = node.getPath();
                            checkerContentUUID = node.getIdentifier();
                            brokenlinksCheckStartPath.setModel(new PropertyModel<String>(BrokenLinksCheckerConfigPanel.this, "checkerStartPath"));
                        } catch (RepositoryException ex) {
                            error(ex.getMessage());
                        }
                    }
                };
            }
        };

        final DialogAction dialogAction = new DialogAction(dialogFactory, getDialogService());
        locationPickLink.add(new AjaxEventBehavior("onclick") {
            @Override
            protected void onEvent(final AjaxRequestTarget target) {
                dialogAction.execute();
            }
        });

        add(locationPickLink);
        add(brokenlinksCheckConnectionTimeoutField);
        add(brokenlinksCheckSocketTimeoutField);
        add(brokenlinksCheckNrOfThreads);
        add(brokenlinksCheckStartPath);
    }

    public void save() {
        BrokenlinksCheckerConfig brokenlinksCheckerConfig = brokenLinksCheckerConfigModel.getObject();

        try {
            brokenlinksCheckerConfig.setStartPathUUID(checkerContentUUID);
            brokenlinksCheckerConfig.save();
        } catch (RepositoryException e) {
            error("An error occurred while trying to save broken links configuration: " + e);
        }
    }

    public void cancel() {
        // empty
    }

    protected IDialogService getDialogService() {
        return getContext().getService(IDialogService.class.getName(), IDialogService.class);
    }

    private AjaxFormComponentUpdatingBehavior createSimpleAjaxChangeBehavior(final Component... components) {
        return new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (components != null) {
                    for (final Component component : components) {
                        target.addComponent(component);
                        Component reset = component.getParent().get(component.getId() + "-reset-container");
                        if (reset != null) {
                            target.addComponent(reset);
                        }
                    }
                }
            }
        };
    }
}
