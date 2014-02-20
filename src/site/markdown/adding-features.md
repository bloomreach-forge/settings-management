<!--
  Copyright 2013 Hippo B.V. (http://www.onehippo.com)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  -->

# Adding configuration options for custom features

Adding or extending the configuration panels with your own Hippo CMS plugin configuration is quite easy.
It basically consists of three steps:

* Create a new Maven project (or add an existing module to your project/plugin)
* Add the required dependencies
* Create a Wicket Model wrapping the JCR node that contains the configuration
* Create a Wicket Panel for displaying the fields in the settings management panel
* Adding the new panel to one of the existing categories (or to a new category)

##Add the API dependency to your project

Add the frontend api to your custom feature module. For the latest version see the [release notes](/release-notes.html) page.
```
<dependency>
  <groupId>org.onehippo.forge.settingsmanagement</groupId>
  <artifactId>hippo-addon-settings-management-frontend-api</artifactId>
  <version>0.2.X</version>
</dependency>
```

##Creating a Wicket Model

Make sure the newly created Java class implement the _CMSFeatureConfig_ interface.

See the example below for a real-life code sample of an object wrapping the User management JCR configuration node.

```java
public class UserManagementConfig implements CMSFeatureConfig {

    private final static Logger log = LoggerFactory.getLogger(UserManagementConfig.class);
    private Boolean userCreationEnabled;
    private transient Node node;

    public UserManagementConfig(Node configNode) {
        init(configNode);
    }

    private void init(Node configNode){
        this.node = configNode;
        try {
            if(node.hasProperty(ListUsersPlugin.USER_CREATION_ENABLED_KEY)) {
                userCreationEnabled = node.getProperty(ListUsersPlugin.USER_CREATION_ENABLED_KEY).getBoolean();
            } else {
                //default
                userCreationEnabled = true;
            }
        } catch (RepositoryException e) {
            log.error("Error: {}", e);
        }
    }

    public Boolean isUserCreationEnabled() {
        return userCreationEnabled;
    }

    public void setUserCreationEnabled(final Boolean userCreationEnabled) {
        this.userCreationEnabled = userCreationEnabled;
    }

    @Override
    public void save() throws RepositoryException {
        node.setProperty(ListUsersPlugin.USER_CREATION_ENABLED_KEY, userCreationEnabled);
        node.getSession().save();
    }

}
```

Now that we have the simple object in place that maps to the JCR node we need to create a DetachableModel for our Wicket Panel.

```java
public class UserManagementConfigModel extends LoadableDetachableConfigModel<UserManagementConfig> {

    public static final String USER_MANAGEMENT_CONFIG_MODEL_PATH = "/hippo:configuration/hippo:frontend/cms/cms-admin/users";

    @Override
    protected UserManagementConfig load() {
        return new UserManagementConfig(getConfigNode(USER_MANAGEMENT_CONFIG_MODEL_PATH));
    }
}
```
##Creating the Wicket Panel

```java
public class UserManagementConfigPanel extends FeatureConfigPanel {

    private UserManagementConfigModel userManagementConfigModel;

    public UserManagementConfigPanel(IPluginContext context, IPluginConfig config) {
        super(context, config, new ResourceModel("title"));

        userManagementConfigModel = new UserManagementConfigModel();

        RadioGroup allowUserCreationRadioGroup =
            new RadioGroup("userlist-allow-usercreation", new PropertyModel(userManagementConfigModel,"userCreationEnabled"));

        allowUserCreationRadioGroup.add(new Radio("usercreation-disabled",new Model(Boolean.FALSE)));
        allowUserCreationRadioGroup.add(new Radio("usercreation-enabled",new Model(Boolean.TRUE)));
        add(allowUserCreationRadioGroup);
    }

    public void save() {
        UserManagementConfig userManagementConfig = userManagementConfigModel.getObject();

        try {
            userManagementConfig.save();
        } catch (RepositoryException e) {
            error("An error occurred while trying to save event log configuration: " + e);
        }
    }

    public void cancel() {
        // do nothing.
    }
}
```

And adding the markup for our panel.

```xml
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:wicket="http://wicket.apache.org">
<wicket:extend>
    <div class="settings-formfield-group">
        <div wicket:id="userlist-allow-usercreation">
            <label class="settings-formfield-label"><wicket:message key="user-creation-cms"/></label>
            <div class="settings-formfield-radio">
                <input type="radio" wicket:id="usercreation-enabled"/><label><wicket:message key="user-creation-on"/></label>
            </div>
            <div class="settings-formfield-radio">
                <input type="radio" wicket:id="usercreation-disabled"/><label><wicket:message key="user-creation-off"/></label>
            </div>
        </div>
    </div>
</wicket:extend>
</html>
```

##Adding the configuration

Create a new XML file in your bootstrap directory with the following content.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<sv:node sv:name="usermanagement" xmlns:sv="http://www.jcp.org/jcr/sv/1.0">
  <sv:property sv:name="jcr:primaryType" sv:type="Name">
    <sv:value>frontend:pluginconfig</sv:value>
  </sv:property>
  <sv:property sv:name="featureConfigClass" sv:type="String">
    <sv:value>UserManagementConfigPanel</sv:value>
  </sv:property>
  <sv:property sv:name="sequence" sv:type="Double">
    <sv:value>1.0</sv:value>
  </sv:property>
</sv:node>
```

As you can see you can control the order of your item in an existing category by changing the sequence property of the node.

Now you also need add this XML file to your _hippoecm-extension.xml_ file.

```xml
<sv:node sv:name="cms-admin-settings-usermanagement">
  <sv:property sv:name="jcr:primaryType" sv:type="Name">
    <sv:value>hippo:initializeitem</sv:value>
  </sv:property>
  <sv:property sv:name="hippo:contentresource" sv:type="String">
    <sv:value>usermanagement.xml</sv:value>
  </sv:property>
  <sv:property sv:name="hippo:contentroot" sv:type="String">
    <sv:value>/hippo:configuration/hippo:frontend/cms/cms-admin/settings/tabconfig/security</sv:value>
  </sv:property>
  <sv:property sv:name="hippo:sequence" sv:type="Double">
    <sv:value>22010.0</sv:value>
  </sv:property>
</sv:node>
```

__Take note__ that the initialize sequence starts at __22000__ for the default settings management configuration.
If you would like add your own configuration it's advisable to put your _hippo:sequence_ number higher than __23000__.

Before you start it's highly recommended to take a look at the [existing code](source-repository.html) for examples and references.