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
# How to install the settings management plugin

## Prerequisites

These instruction assumes that you have an Hippo CMS project based on the Hippo website archetype, i.e. a Maven multi-module project
consisting of at least three sub-modules: cms, site and content.

### Maven 3 repository (optional)
Add the following repository to your root pom.xml (not needed if you use no custom parent pom.xml ):

```
<repository>
  <id>hippo-forge</id>
  <name>Hippo Forge maven 3 repository.</name>
  <url>http://maven.onehippo.com/maven2-forge/</url>
  <snapshots>
    <enabled>false</enabled>
  </snapshots>
  <releases>
    <updatePolicy>never</updatePolicy>
  </releases>
  <layout>default</layout>
</repository>
```


### Installation in CMS/Repository

Add these two dependency to the pom.xml of your *cms* module:

```
<dependency>
  <groupId>org.onehippo.forge.settingsmanagement</groupId>
  <artifactId>hippo-addon-settings-management-repository</artifactId>
  <version>0.2.1</version>
</dependency>

<dependency>
  <groupId>org.onehippo.forge.settingsmanagement</groupId>
  <artifactId>hippo-addon-settings-management-frontend-core</artifactId>
  <version>0.2.1</version>
</dependency>

```

Rebuild your project. In case you start with an existing repository don't forget to add *-Drepo.bootstrap=true*
to your startup options.