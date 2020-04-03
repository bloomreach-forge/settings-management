/*
 * Copyright 2020 Bloomreach Inc. (http://www.bloomreach.com)
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
package org.bloomreach.forge.settings.management.config.crispapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.bloomreach.forge.settings.management.FeatureConfigPanel;
import org.hippoecm.frontend.dialog.AbstractDialog;
import org.hippoecm.frontend.dialog.DialogLink;
import org.hippoecm.frontend.dialog.IDialogFactory;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.cms.admin.widgets.AdminDataTable;
import org.hippoecm.frontend.plugins.cms.admin.widgets.AjaxLinkLabel;
import org.hippoecm.frontend.plugins.standards.ClassResourceModel;
import org.hippoecm.frontend.session.UserSession;
import org.hippoecm.frontend.widgets.TextFieldWidget;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.repository.l10n.LocalizationService;
import org.onehippo.repository.l10n.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrispApiConfigPanel extends FeatureConfigPanel {

    private static Logger log = LoggerFactory.getLogger(CrispApiConfigPanel.class);

    private static final int RESOURCE_SPACES_PAGE_SIZE = 5;

    private static final int RESOURCE_SPACE_PROPS_PAGE_SIZE = 10;

    private CrispApiConfigModel crispApiConfigModel;

    private CrispResourceSpace currentCrispResourceSpace;

    private final AdminDataTable<CrispResourceSpace> resourceSpacesTable;

    private final Label curResourceSpacePropsLabel;

    private final AdminDataTable<CrispResourceSpaceProperty> resourceSpacePropsTable;

    private ResourceBundle backendTypeResourceBundle;

    public CrispApiConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        crispApiConfigModel = new CrispApiConfigModel(config);

        final LocalizationService localizationService = HippoServiceRegistry.getService(LocalizationService.class);

        if (localizationService != null) {
            backendTypeResourceBundle = localizationService
                    .getResourceBundle(CrispApiConfigConstants.BACKEND_TYPE_BUNDLE_NAME, UserSession.get().getLocale());
        }

        final boolean crispConfigInstalled = crispApiConfigModel.getObject().hasConfiguration();

        final Label notInstalledMessage = new Label("not-installed",
                new ClassResourceModel("not-installed", CrispApiConfigPanel.class));
        notInstalledMessage.setVisible(!crispConfigInstalled);
        add(notInstalledMessage);

        final DialogLink addResourceSpace = new DialogLink("addResourceSpace",
                new ResourceModel("add-new-resource-space"), new IDialogFactory() {
                    public AbstractDialog<CrispResourceSpace> createDialog() {
                        return new CrispResourceSpaceAddDialog(Model.of(new CrispResourceSpace()), crispApiConfigModel,
                                CrispApiConfigPanel.this, backendTypeResourceBundle);
                    }
                }, context.getService(IDialogService.class.getName(), IDialogService.class));
        addResourceSpace.setVisible(crispConfigInstalled);
        add(addResourceSpace);

        final CrispResourceSpaceDataProvider crispResourceSpaceDataProvider = new CrispResourceSpaceDataProvider(
                crispApiConfigModel);

        final List<IColumn<CrispResourceSpace, String>> resourceSpacesColumns = new ArrayList<>();
        resourceSpacesColumns.add(new AbstractColumn<CrispResourceSpace, String>(
                new ResourceModel("resource-space-name"), "resourceSpaceName") {
            @Override
            public void populateItem(final Item<ICellPopulator<CrispResourceSpace>> cellItem, final String componentId,
                    final IModel<CrispResourceSpace> rowModel) {
                cellItem.add(new AjaxLinkLabel(componentId, Model.of(rowModel.getObject().getResourceSpaceName())) {
                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        setCurrentCrispResourceSpace(rowModel.getObject());
                        target.add(curResourceSpacePropsLabel);
                        target.add(resourceSpacePropsTable);
                    }
                });
            }
        });
        resourceSpacesColumns.add(new PropertyColumn<CrispResourceSpace, String>(new ResourceModel("backend-type-name"),
                "backendTypeName", "backendTypeName") {
            @Override
            public IModel<?> getDataModel(IModel<CrispResourceSpace> rowModel) {
                final String backendTypeName = rowModel.getObject().getBackendTypeName();
                return Model
                        .of((backendTypeResourceBundle != null)
                                ? StringUtils.defaultIfBlank(backendTypeResourceBundle.getString(backendTypeName),
                                        backendTypeName)
                                : backendTypeName);
            }
        });
        resourceSpacesColumns.add(new AbstractColumn<CrispResourceSpace, String>(Model.of("")) {
            @Override
            public void populateItem(final Item<ICellPopulator<CrispResourceSpace>> cellItem, final String componentId,
                    final IModel<CrispResourceSpace> rowModel) {
                cellItem.add(new AjaxLinkLabel(componentId, Model.of(" ")) {
                    {
                        get("link").add(new AttributeModifier("class", "delete-16 icon-16"));
                    }

                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        final String resourceSpaceName = rowModel.getObject().getResourceSpaceName();
                        crispApiConfigModel.getObject().removeCurrentCrispResourceSpace(resourceSpaceName);

                        target.add(resourceSpacesTable);
                        target.add(resourceSpacePropsTable);

                        if (currentCrispResourceSpace != null && StringUtils.equals(resourceSpaceName,
                                currentCrispResourceSpace.getResourceSpaceName())) {
                            currentCrispResourceSpace = null;
                            target.add(curResourceSpacePropsLabel);
                        }
                    }
                });
            }
        });

        curResourceSpacePropsLabel = new Label("current-resource-space-name",
                new PropertyModel<String>(this, "currentCrispResourceSpaceName"));
        curResourceSpacePropsLabel.setOutputMarkupId(true);
        add(curResourceSpacePropsLabel);

        resourceSpacesTable = new AdminDataTable<>("resourceSpacesTable", resourceSpacesColumns,
                crispResourceSpaceDataProvider, RESOURCE_SPACES_PAGE_SIZE);
        resourceSpacesTable.setOutputMarkupId(true);
        add(resourceSpacesTable);

        final CrispResourceSpacePropertyDataProvider crispResourceSpacePropertyDataProvider = new CrispResourceSpacePropertyDataProvider();

        final List<IColumn<CrispResourceSpaceProperty, String>> resourceSpacePropsColumns = new ArrayList<>();
        resourceSpacePropsColumns.add(new PropertyColumn<>(new ResourceModel("property-name"), "name", "name"));
        resourceSpacePropsColumns.add(
                new AbstractColumn<CrispResourceSpaceProperty, String>(new ResourceModel("property-value"), "value") {
                    @Override
                    public void populateItem(final Item<ICellPopulator<CrispResourceSpaceProperty>> cellItem,
                            final String componentId, final IModel<CrispResourceSpaceProperty> rowModel) {
                        cellItem.add(new TextFieldWidget(componentId, new LoadableDetachableModel<String>() {
                            @Override
                            public void setObject(final String value) {
                                rowModel.getObject().setValue(value);
                                detach();
                            }

                            @Override
                            protected String load() {
                                return rowModel.getObject().getValue();
                            }
                        }) {
                            @Override
                            protected void onUpdate(final AjaxRequestTarget target) {
                                target.add(resourceSpacePropsTable);
                            }
                        });
                    }
                });

        resourceSpacePropsTable = new AdminDataTable<>("resourceSpacePropsTable", resourceSpacePropsColumns,
                crispResourceSpacePropertyDataProvider, RESOURCE_SPACE_PROPS_PAGE_SIZE);
        resourceSpacePropsTable.setOutputMarkupId(true);
        add(resourceSpacePropsTable);
    }

    @Override
    public void save() {
        try {
            crispApiConfigModel.getObject().save();
        } catch (RepositoryException e) {
            log.error("Failed to save crisp resource space configurations.", e);
        }
    }

    @Override
    public void cancel() {
        // do nothing.
    }

    public CrispResourceSpace getCurrentCrispResourceSpace() {
        return currentCrispResourceSpace;
    }

    public void setCurrentCrispResourceSpace(final CrispResourceSpace currentCrispResourceSpace) {
        this.currentCrispResourceSpace = currentCrispResourceSpace;
    }

    public String getCurrentCrispResourceSpaceName() {
        return (currentCrispResourceSpace != null) ? currentCrispResourceSpace.getResourceSpaceName() : null;
    }

    public void refreshCrispResourceSpacesTable(final AjaxRequestTarget target) {
        target.add(resourceSpacesTable);

        currentCrispResourceSpace = null;
        target.add(curResourceSpacePropsLabel);
        target.add(resourceSpacePropsTable);
    }

    private class CrispResourceSpacePropertyDataProvider
            extends SortableDataProvider<CrispResourceSpaceProperty, String> {

        @Override
        public long size() {
            if (currentCrispResourceSpace == null) {
                return 0L;
            }

            return currentCrispResourceSpace.getProperties().size();
        }

        @Override
        public IModel<CrispResourceSpaceProperty> model(CrispResourceSpaceProperty object) {
            return new Model<>(object);
        }

        @Override
        public Iterator<? extends CrispResourceSpaceProperty> iterator(long first, long count) {
            if (currentCrispResourceSpace == null) {
                return Collections.<CrispResourceSpaceProperty> emptyList().iterator();
            }

            final List<CrispResourceSpaceProperty> propsList = new LinkedList<>(
                    currentCrispResourceSpace.getProperties());

            final SortParam<String> sortParam = getSort();

            if (sortParam != null) {
                propsList.sort((prop1, prop2) -> {
                    final int direction = getSort().isAscending() ? 1 : -1;
                    switch (getSort().getProperty()) {
                    case "value":
                        return direction * StringUtils.compareIgnoreCase(prop1.getValue(), prop2.getValue());
                    case "name":
                    default:
                        return direction * StringUtils.compareIgnoreCase(prop1.getName(), prop2.getName());
                    }
                });
            }

            if (first == 0L && propsList.size() <= count) {
                return propsList.iterator();
            }

            return propsList.subList((int) first, (int) Math.min(first + count, propsList.size())).iterator();
        }
    }
}
