<!--
  Copyright 2014-2022 Bloomreach Inc. (https://www.bloomreach.com)

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
## <span style="color:red">To be retired</span>

<div class="alert alert-danger">
This plugin no longer works when used on Experience Manager 14.1 and up, because of a refactoring of the Admin 
Perspective, see issue https://issues.onehippo.com/browse/CMS-11846.

Therefore, the plugin is no longer maintained and will probably be retired, unless a large community effort is
contributed.
</div>

## How to install the settings management plugin

### Prerequisites

These instruction assumes that you have a Bloomreach CMS project based on the archetype, i.e. a Maven 
multi-module project consisting of at least four sub-modules: cms, cms-dependencies, site and bootstrap.

### Forge Repository
In the main pom.xml of the project, in the repositories section, add this repository if it is not configured yet. 

```
<repository>
  <id>hippo-forge</id>
  <name>Bloomreach Forge maven 2 repository.</name>
  <url>https://maven.onehippo.com/maven2-forge/</url>
  <snapshots>
    <enabled>false</enabled>
  </snapshots>
  <releases>
    <updatePolicy>never</updatePolicy>
  </releases>
  <layout>default</layout>
</repository>
```

### Installation in CMS
Add this property to the properties section of the *root* pom.xml:

    <bloomreach.forge.settingsmanagement.version>3.1.0</bloomreach.forge.settingsmanagement.version>

Select the correct version for your project. See the [release notes](release-notes.html) for more information on which version is applicable.

Add these two dependency to the pom.xml of your *cms-dependencies* (or *cms*) module:

```
<dependency>
  <groupId>org.bloomreach.forge.settingsmanagement</groupId>
  <artifactId>bloomreach-settingsmanagement-repository</artifactId>
  <version>${bloomreach.forge.settingsmanagement.version}</version>
</dependency>

<dependency>
  <groupId>org.bloomreach.forge.settingsmanagement</groupId>
  <artifactId>bloomreach-settingsmanagement-frontend-core</artifactId>
  <version>${bloomreach.forge.settingsmanagement.version}</version>
</dependency>

```

<div class="alert alert-info">
    Note: before version 2.0.0, the artifacts' groupId was <code>org.onehippo.forge.settingsmanagement</code> and
    the artifactIds started with <code>hippo-addon-settings-management</code>. 
</div>

Rebuild and start your project. In case you start with an existing repository don't forget to add *-Drepo.bootstrap=true*
to your startup options.

