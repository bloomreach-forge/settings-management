/*
 * Copyright 2020 Bloomreach Inc. (http://www.bloomreach.com)
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
package org.bloomreach.forge.settings.management.config.crispapi;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.value.IValueMap;

import org.hippoecm.frontend.dialog.Dialog;
import org.hippoecm.frontend.dialog.DialogConstants;
import org.hippoecm.frontend.plugins.standards.ClassResourceModel;
import org.onehippo.repository.l10n.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrispResourceSpaceAddDialog extends Dialog<CrispResourceSpace> {

    private static Logger log = LoggerFactory.getLogger(CrispResourceSpaceAddDialog.class);

    private final CrispApiConfigModel crispApiConfigModel;
    private final CrispApiConfigPanel configPanel;
    private final ResourceBundle backendTypeResourceBundle;

    public CrispResourceSpaceAddDialog(final IModel<CrispResourceSpace> model,
            final CrispApiConfigModel crispApiConfigModel, final CrispApiConfigPanel configPanel,
            final ResourceBundle backendTypeResourceBundle) {
        super(model);

        this.crispApiConfigModel = crispApiConfigModel;
        this.configPanel = configPanel;
        this.backendTypeResourceBundle = backendTypeResourceBundle;

        final TextField<String> resourceSpaceNameField = new TextField<>("resourceSpaceName",
                new PropertyModel<String>(model, "resourceSpaceName"));
        resourceSpaceNameField.setOutputMarkupId(true);
        add(resourceSpaceNameField);

        final List<String> backendTypeNames = crispApiConfigModel.getObject().getCrispResourceSpaceTemplates().stream()
                .map(item -> item.getBackendTypeName()).collect(Collectors.toList());
        final DropDownChoice<String> backendTypeField = new DropDownChoice<>("backendTypeName",
                new PropertyModel<String>(model, "backendTypeName"), backendTypeNames, new IChoiceRenderer<String>() {
                    @Override
                    public Object getDisplayValue(String object) {
                        return (backendTypeResourceBundle != null)
                                ? StringUtils.defaultIfBlank(backendTypeResourceBundle.getString(object), object)
                                : object;
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }

                    @Override
                    public String getObject(String id, IModel<? extends List<? extends String>> choices) {
                        return id;
                    }
                });
        backendTypeField.setOutputMarkupId(true);
        backendTypeField.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                final CrispResourceSpace modelObject = CrispResourceSpaceAddDialog.this.getModel().getObject();
                if (StringUtils.isNotBlank(modelObject.getBackendTypeName())
                        && StringUtils.isBlank(modelObject.getResourceSpaceName())) {
                    modelObject.setResourceSpaceName(StringUtils.removeStart(modelObject.getBackendTypeName(),
                            CrispApiConfigConstants.BRX_BACKEND_TYPE_NAME_PREFIX));
                    target.add(resourceSpaceNameField);
                }
            }
        });

        add(backendTypeField);
    }

    @Override
    public IModel<String> getTitle() {
        return new ClassResourceModel("title", CrispResourceSpaceAddDialog.class);
    }

    @Override
    public IValueMap getProperties() {
        return DialogConstants.MEDIUM_AUTO;
    }

    @Override
    protected void onOk() {
        final CrispResourceSpace newResourceSpace = getModel().getObject();

        if (StringUtils.isBlank(newResourceSpace.getBackendTypeName())) {
            error(new ClassResourceModel("error-blank-backend-type-name", CrispResourceSpaceAddDialog.class)
                    .getObject());
            return;
        }

        if (StringUtils.isBlank(newResourceSpace.getResourceSpaceName())) {
            error(new ClassResourceModel("error-blank-resource-space-name", CrispResourceSpaceAddDialog.class)
                    .getObject());
            return;
        }

        final CrispApiConfig crispApiConfig = crispApiConfigModel.getObject();

        final CrispResourceSpace existing = crispApiConfig
                .getCurrentCrispResourceSpaceByResourceSpaceName(newResourceSpace.getResourceSpaceName());

        if (existing != null) {
            error(new ClassResourceModel("error-resource-space-name-exists", CrispResourceSpaceAddDialog.class)
                    .getObject());
            return;
        }

        final CrispResourceSpace template = crispApiConfig
                .getCrispResourceSpaceTemplateByBackendTypeName(newResourceSpace.getBackendTypeName());

        if (template != null) {
            newResourceSpace.setBeansDefinition(template.getBeansDefinition());

            for (CrispResourceSpaceProperty prop : template.getProperties()) {
                newResourceSpace.addProperty((CrispResourceSpaceProperty) prop.clone());
            }
        } else {
            log.error("Cannot find the crisp resource space template by the backend type name, '{}'.",
                    newResourceSpace.getBackendTypeName());
        }

        crispApiConfig.addCurrentCrispResourceSpace(newResourceSpace);
    }

    @Override
    public void onClose() {
        super.onClose();
        final AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        configPanel.refreshCrispResourceSpacesTable(target);
    }
}
