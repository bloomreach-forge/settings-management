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

package org.onehippo.forge.settings.management.config.autoexport;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.settings.management.FeatureConfigPanel;

public class AutoExportConfigPanel extends FeatureConfigPanel {
    private AutoExportConfigModel autoExportConfigModel;

    public AutoExportConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        autoExportConfigModel = new AutoExportConfigModel();

        CheckBox autoexportEnabled = new CheckBox("autoexport-enabled", new PropertyModel(autoExportConfigModel, "enabled"));
        add(autoexportEnabled);

        addModulesConfigurationToForm();
    }

    public void save() {
        AutoExportConfig autoExportConfig = autoExportConfigModel.getObject();
        autoExportConfig.save();
    }

    public void cancel() {
        // do nothing.
    }

    private void addModulesConfigurationToForm() {

        final WebMarkupContainer listContainer = new WebMarkupContainer("modulesContainer");
        //generate a markup-id so the contents can be updated through an AJAX call
        listContainer.setOutputMarkupId(true);

        final AutoExportConfig autoExportConfig = autoExportConfigModel.getObject();
        //final ArrayList<String> modulesList = autoExportConfig.getModules();
        ListView<String> modules = new ListView<String>("autoexport-modules", new PropertyModel(autoExportConfigModel, "modules")) {
            @Override
            protected void populateItem(final ListItem<String> item) {
                RequiredTextField moduleField = new RequiredTextField("module", item.getModel());
                moduleField.setOutputMarkupId(true);
                moduleField.add(new OnChangeAjaxBehavior() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {
                    }
                });
                item.add(moduleField);

                AjaxSubmitLink remove = new AjaxSubmitLink("remove") {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form form) {
                        //modulesList.remove(item.getIndex());
                        //autoExportConfigModel.getObject().setModules(modulesList);
                        target.add(listContainer);
                    }
                };
                remove.setDefaultFormProcessing(false);
                item.add(remove);
            }
        };

        modules.setReuseItems(true);
        listContainer.add(modules);
        add(listContainer);
    }
}
