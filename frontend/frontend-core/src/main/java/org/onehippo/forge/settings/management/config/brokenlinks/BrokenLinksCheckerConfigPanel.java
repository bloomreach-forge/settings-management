/*
 * Copyright 2013-2019 BloomReach Inc. (http://www.bloomreach.com)
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
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.hippoecm.frontend.PluginRequestTarget;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.DialogAction;
import org.hippoecm.frontend.dialog.IDialogFactory;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.editor.plugins.linkpicker.LinkPickerDialog;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.settings.management.FeatureConfigPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrokenLinksCheckerConfigPanel extends FeatureConfigPanel {
    private static final long serialVersionUID = 1L;
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

        RadioGroup linksCheckEnabledRadioGroup = new RadioGroup("linkscheck-enabledgroup", new PropertyModel(brokenLinksCheckerConfigModel, "enabled"));
        linksCheckEnabledRadioGroup.add(new Radio("linkscheck-disabled", new Model(Boolean.FALSE)));
        linksCheckEnabledRadioGroup.add(new Radio("linkscheck-enabled", new Model(Boolean.TRUE)));

        TextField cronExpressionField = new TextField("linkscheck-cronexpression", new PropertyModel(brokenLinksCheckerConfigModel, "cronExpression"));
        TextField brokenlinksCheckConnectionTimeoutField = new TextField("linkscheck-connectionTimeout", new PropertyModel(brokenLinksCheckerConfigModel,"connectionTimeout"));
        TextField brokenlinksCheckSocketTimeoutField = new TextField("linkscheck-socketTimeout", new PropertyModel(brokenLinksCheckerConfigModel,"socketTimeout"));
        TextField brokenlinksCheckNrHttpThreads = new TextField("linkscheck-nrHttpThreads", new PropertyModel(brokenLinksCheckerConfigModel,"nrHttpThreads"));
        TextField brokenlinksUrlExcludes = new TextField("linkscheck-urlExcludes", new PropertyModel(brokenLinksCheckerConfigModel,"urlExcludes"));

        final TextField brokenlinksCheckStartPath = new TextField("linkscheck-startPath", new PropertyModel(this, "checkerStartPath"));
        final Image locationPickLink = new Image("linkcheck-startPath-location-search", new PackageResourceReference(BrokenLinksCheckerConfigPanel.class, "folder-choose.png")) {
            private static final long serialVersionUID = 1L;
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
                    private static final long serialVersionUID = 1L;
                    @Override
                    public void render(PluginRequestTarget target) {
                        target.add(brokenlinksCheckStartPath);
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
            private static final long serialVersionUID = 1L;
            @Override
            protected void onEvent(final AjaxRequestTarget target) {
                dialogAction.execute();
            }
        });

        add(linksCheckEnabledRadioGroup);
        add(cronExpressionField);
        add(locationPickLink);
        add(brokenlinksCheckConnectionTimeoutField);
        add(brokenlinksCheckSocketTimeoutField);
        add(brokenlinksCheckNrHttpThreads);
        add(brokenlinksCheckStartPath);
        add(brokenlinksUrlExcludes);
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
            private static final long serialVersionUID = 1L;
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (components != null) {
                    for (final Component component : components) {
                        target.add(component);
                        Component reset = component.getParent().get(component.getId() + "-reset-container");
                        if (reset != null) {
                            target.add(reset);
                        }
                    }
                }
            }
        };
    }
}
