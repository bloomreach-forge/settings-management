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

package org.onehippo.forge.settings.management;

import java.util.List;
import java.util.ArrayList;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: tjeger Date: 2/8/13 Time: 11:47 AM To change this template use File | Settings |
 * File Templates.
 */
public class TabPanel extends Panel {

    private final static Logger log = LoggerFactory.getLogger(TabPanel.class);
    private IPluginContext context;
    private IPluginConfig config;
    private IBreadCrumbModel breadCrumbModel;
    private List<FeatureConfigPanel> features = new ArrayList<FeatureConfigPanel>();

    public TabPanel(final String id, final IBreadCrumbModel breadCrumbModel,
                    final IPluginContext context, final IPluginConfig config,
                    final List<FeatureConfigPanel> features) {
        super(id);
        this.config = config;
        this.context = context;
        this.breadCrumbModel = breadCrumbModel;
        this.features = new ArrayList<FeatureConfigPanel>();
        if (features != null) {
            this.features.addAll(features);
        }

        // create feedback panel to show errors
        final FeedbackPanel feedback = new FeedbackPanel("feedback");
        feedback.setOutputMarkupId(true);
        add(feedback);

        final Form form = new Form("form");

        form.add(new AjaxButton("save-button", form) {
            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                for (FeatureConfigPanel feature : features) {
                    feature.save();
                }
                target.addComponent(form);
                target.addComponent(feedback);
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                target.addComponent(form);
                target.addComponent(feedback);
            }
        });

        form.add(new AjaxButton("cancel-button") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                for (FeatureConfigPanel feature : features) {
                    feature.cancel();
                }

                // one up
                List<IBreadCrumbParticipant> l = breadCrumbModel.allBreadCrumbParticipants();
                breadCrumbModel.setActive(l.get(l.size() - 2));
            }
        }.setDefaultFormProcessing(false));

        // add a ListView containing all features to the form
        final ListView<FeatureConfigPanel> listView = new ListView<FeatureConfigPanel>("feature-list", features) { // or use a provider
            @Override
            protected void populateItem(final ListItem<FeatureConfigPanel> item) {
                FeatureConfigPanel feature = item.getModelObject();
                item.add(feature);
            }
        };
        listView.setRenderBodyOnly(true);
        form.add(listView);

        add(form);
    }
}
