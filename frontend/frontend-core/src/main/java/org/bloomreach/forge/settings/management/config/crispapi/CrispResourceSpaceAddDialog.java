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

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.value.IValueMap;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.DialogConstants;
import org.hippoecm.frontend.plugins.standards.ClassResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrispResourceSpaceAddDialog extends AbstractDialog<CrispResourceSpace> {

    private static Logger log = LoggerFactory.getLogger(CrispResourceSpaceAddDialog.class);

    private final CrispApiConfigModel crispApiConfigModel;
    private final CrispApiConfigPanel configPanel;

    public CrispResourceSpaceAddDialog(final IModel<CrispResourceSpace> model,
            final CrispApiConfigModel crispApiConfigModel, final CrispApiConfigPanel configPanel) {
        super(model);

        this.crispApiConfigModel = crispApiConfigModel;
        this.configPanel = configPanel;

        final TextField<String> resourceSpaceNameField = new TextField<>("resourceSpaceName",
                new PropertyModel<String>(model, "resourceSpaceName"));
        resourceSpaceNameField.setOutputMarkupId(true);
        add(resourceSpaceNameField);

        final DropDownChoice<String> backendTypeField = new DropDownChoice<>("backendTypeName",
                new PropertyModel<String>(model, "backendTypeName"),
                crispApiConfigModel.getObject().getAvailableBackendTypeNames());
        backendTypeField.setOutputMarkupId(true);
        backendTypeField.add(new OnChangeAjaxBehavior() {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                final CrispResourceSpace modelObject = CrispResourceSpaceAddDialog.this.getModel().getObject();
                if (StringUtils.isNotBlank(modelObject.getBackendTypeName())
                        && StringUtils.isBlank(modelObject.getResourceSpaceName())) {
                    modelObject.setResourceSpaceName(modelObject.getBackendTypeName());
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
        return DialogConstants.SMALL;
    }

    @Override
    protected void onOk() {
        final CrispResourceSpace updated = getModel().getObject();

        if (StringUtils.isBlank(updated.getBackendTypeName())) {
            error(new ClassResourceModel("error-blank-backend-type-name", CrispResourceSpaceAddDialog.class)
                    .getObject());
            return;
        }

        if (StringUtils.isBlank(updated.getResourceSpaceName())) {
            error(new ClassResourceModel("error-blank-resource-space-name", CrispResourceSpaceAddDialog.class)
                    .getObject());
            return;
        }

        final CrispResourceSpace existing = crispApiConfigModel.getObject()
                .getCrispResourceSpaceByName(updated.getResourceSpaceName());

        if (existing != null) {
            error(new ClassResourceModel("error-resource-space-name-exists", CrispResourceSpaceAddDialog.class)
                    .getObject());
            return;
        }

        crispApiConfigModel.getObject().addCrispResourceSpace(updated);
    }

    @Override
    public void onClose() {
        super.onClose();
        final AjaxRequestTarget target = RequestCycle.get().find(AjaxRequestTarget.class);
        configPanel.refreshCrispResourceSpacesTable(target);
    }
}
