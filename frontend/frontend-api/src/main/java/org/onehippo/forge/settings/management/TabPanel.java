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

package org.onehippo.forge.settings.management;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.feedback.IFeedbackMessageFilter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.session.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TabPanel extends Panel {

    private static final long serialVersionUID = 1L;
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
        final FeedbackPanel feedback = new FeedbackPanel("feedback", new IFeedbackMessageFilter() {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean accept(final FeedbackMessage message) {
                return !message.isRendered();
            }
        });
        feedback.setOutputMarkupId(true);

        final Form form = new Form("form");

        form.add(new AjaxButton("save-button", form) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                for (FeatureConfigPanel feature : features) {
                    feature.save();
                }
                target.add(form);
                info(getString("data-saved"));
                target.add(feedback);
            }

            @Override
            protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                target.add(form);
                target.add(feedback);
            }
        });

        form.add(new AjaxButton("cancel-button") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                for (FeatureConfigPanel feature : features) {
                    feature.cancel();
                }
                try {
                    UserSession.get().getJcrSession().refresh(false);
                } catch (RepositoryException e) {
                    log.error("An exception occurred while trying to refresh the session: {}", e);
                }
                // one up
                List<IBreadCrumbParticipant> l = breadCrumbModel.allBreadCrumbParticipants();
                breadCrumbModel.setActive(l.get(l.size() - 2));
            }
        }.setDefaultFormProcessing(false));

        form.add(feedback);

        // add a ListView containing all features to the form
        final ListView<FeatureConfigPanel> listView = new ListView<FeatureConfigPanel>("feature-list", features) { // or use a provider
            private static final long serialVersionUID = 1L;
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
