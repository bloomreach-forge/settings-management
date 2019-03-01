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

package org.bloomreach.forge.settings.management.config.social;

import java.util.List;

import javax.jcr.RepositoryException;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.validation.validator.StringValidator;
import org.apache.wicket.validation.validator.UrlValidator;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.bloomreach.forge.settings.management.FeatureConfigPanel;
import org.bloomreach.forge.settings.management.ValidationStyleBehavior;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SocialMediaConfigPanel extends FeatureConfigPanel {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(SocialMediaConfigPanel.class);

    private SocialMediaServiceConfigModel socialMediaServiceConfigModel;

    public SocialMediaConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        socialMediaServiceConfigModel = new SocialMediaServiceConfigModel();

        final WebMarkupContainer listContainer = new WebMarkupContainer("servicesView");
        listContainer.setOutputMarkupId(true);

        final SocialMediaServiceListView socialservicesView = new SocialMediaServiceListView("socialservices", socialMediaServiceConfigModel.getObject().getServices());

        AjaxLink addService = new AjaxLink("add-service") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                final SocialMediaService newService = socialMediaServiceConfigModel.getObject().addService("unknown");
                if(newService!=null) {
                    socialservicesView.getModelObject().add(newService);
                }
                target.add(listContainer);
                target.focusComponent(this);
            }
        };

        listContainer.add(socialservicesView);
        listContainer.add(addService);
        add(listContainer);
    }

    public void save() {
        try {
            socialMediaServiceConfigModel.getObject().save();
        } catch (RepositoryException e) {
            log.warn("An exception occurred while trying to save: {}",e);
        }
    }

    public void cancel() {
        // do nothing.
    }

    private static final class SocialMediaServiceListView extends ListView<SocialMediaService>
    {
        private static final long serialVersionUID = 1L;
        public SocialMediaServiceListView(String id, List<SocialMediaService> serviceList)
        {
            super(id,serviceList);
            // always do this in forms!
            setReuseItems(true);
        }

        @Override
        protected void populateItem(ListItem<SocialMediaService> item){
            //TODO: http://www.jroller.com/karthikg/entry/wicket_and_ajax
            final Label label = new Label("name", new PropertyModel<String>(item.getModel(), "name"));
            label.setOutputMarkupId(true);
            item.add(label);

            final RequiredTextField<String> displayname = new RequiredTextField<String>("displayname", new PropertyModel<String>(item.getModel(), "name"));
            displayname.add(new StringValidator(1, null));
            displayname.add(new ValidationStyleBehavior());

            item.add(displayname);
            item.add(new CheckBox("enabled",new PropertyModel<Boolean>(item.getModel(),"enabled")));
            final RequiredTextField<String> urlField = new RequiredTextField<String>("url", new PropertyModel<String>(item.getModel(), "url"));
            urlField.add(new UrlValidator());
            item.add(urlField);
            urlField.add(new ValidationStyleBehavior());
            OnChangeAjaxBehavior onChangeAjaxBehavior = new OnChangeAjaxBehavior()
            {
                private static final long serialVersionUID = 1L;
                @Override
                protected void onUpdate(AjaxRequestTarget target)
                {
                    label.setDefaultModelObject(getValue(displayname.getDefaultModelObjectAsString()));
                    target.add(label);
                }
            };
            displayname.add(onChangeAjaxBehavior);
        }

        protected String getValue(String input) {
            if (Strings.isEmpty(input)) {
                return "";
            } else {
                return input;
            }
        }
    }
}
