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
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.bloomreach.forge.settings.management.FeatureConfigPanel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.cms.admin.widgets.AdminDataTable;
import org.hippoecm.frontend.plugins.cms.admin.widgets.AjaxLinkLabel;
import org.hippoecm.frontend.plugins.standards.ClassResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrispApiConfigPanel extends FeatureConfigPanel {

    private static Logger log = LoggerFactory.getLogger(CrispApiConfigPanel.class);

    private static final int NUMBER_OF_ITEMS_PER_PAGE = 20;

    private final AdminDataTable<CrispResourceSpace> table;
    private final CrispResourceSpaceDataProvider crispResourceSpaceDataProvider;

    private CrispApiConfigModel crispApiConfigModel;

    public CrispApiConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        crispApiConfigModel = new CrispApiConfigModel();

        final Label notInstalledMessage = new Label("not-installed",
                new ClassResourceModel("not-installed", CrispApiConfigPanel.class));
        notInstalledMessage.setVisible(!crispApiConfigModel.getObject().hasConfiguration());
        add(notInstalledMessage);

        crispResourceSpaceDataProvider = new CrispResourceSpaceDataProvider();

        final List<IColumn<CrispResourceSpace, String>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<>(new ResourceModel("backend-type"), "backendType", "backendType"));
        columns.add(new AbstractColumn<CrispResourceSpace, String>(new ResourceModel("resource-space-name"), "resourceSpaceName") {
            @Override
            public void populateItem(final Item<ICellPopulator<CrispResourceSpace>> cellItem, final String componentId,
                                     final IModel<CrispResourceSpace> rowModel) {
                cellItem.add(new AjaxLinkLabel(componentId, Model.of(rowModel.getObject().getResourceSpaceName())) {
                    // TODO
                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        log.error("Resource space clicked: {}.", getDefaultModelObject());
                    }
                });
            }
        });

        table = new AdminDataTable<>("table", columns, crispResourceSpaceDataProvider, NUMBER_OF_ITEMS_PER_PAGE);
        table.setOutputMarkupId(true);
        add(table);
    }

    @Override
    public void save() {
        // TODO
    }

    @Override
    public void cancel() {
        // do nothing.
    }
}
