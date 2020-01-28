<!--
  Copyright 2013-2020 Bloomreach Inc. (https://www.bloomreach.com)

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

## Release notes

### Version 3.0.0 
Release date: 29 January 2020

+ Upgrade to Bloomreach Experience Manager 14, including the demo project.<br/>
  The "Other" section (tab) was removed since it contained only settings for the Social Sharing plugin, which was
  removed from version 14.   
+ The translations of configured tabs moved from hippo:translation subnodes below<br/>
 ```/hippo:configuration/hippo:frontend/cms/cms-admin/settings/tabconfig``` to <br/>
 ```/hippo:configuration/hippo:translations/hippo:cms/settingsmanagement```.

### Version 2.0.0 
Release date: 2 March 2019

+ [HIPFORGE-278](https://issues.onehippo.com/browse/HIPFORGE-278)<br/> 
  Upgrade to Bloomreach Experience Manager 13, including the demo project. 
  This also entails renaming of Maven coordinates and Java packaging from 'onehippo' to 'bloomreach'. 
  When upgrading, please revisit the [installation page](install.html).
+ [HIPFORGE-271](https://issues.onehippo.com/browse/HIPFORGE-271)<br/>
  In URL Rewriter tab, add a field for the disallowed duplicate headers.
+ [HIPFORGE-279](https://issues.onehippo.com/browse/HIPFORGE-279)<br/>
  Remove the BrokenLinksChecker panel since the checker is no longer present in 13.
+ [HIPFORGE-280](https://issues.onehippo.com/browse/HIPFORGE-280)<br/>
  Make add/remove form data exclude paths work.

### Version 1.0.0 
Release date: 26 Feb 2018

  Upgrade to Bloomreach CMS 12, including demo project. 
+ Textual improvements, in Dutch mainly.

### Version 0.6.0 
Release date: 24 Jan 2018

+ Added a tab with Url Rewriter settings
+ Upgraded project + demo to Hippo 11.2

### Version 0.5.0 
Release date: 15 Aug 2016

+ Upgraded project + demo to Hippo 11

### Version 0.4.0-milestone-2 
Release date: 21 June 2015

+ HIPPLUG-1136: Incorrect and thereafter failing save of BrokenLinksChecker scheduler configuration

### Version 0.4.0-milestone-1 
Release date: 16 June 2015

+ Test release upgraded to Hippo 10.

### Version 0.3.0 
Release date: 21 May 2014

+ Upgraded to Hippo 7.9. Use 0.2.x for Hippo 7.8. (thanks to Unico Hommes)
+ Updated the configuration options of the Brokenlinkschecker to be compliant with 7.9

### Version 0.2.2
Release date: 21 May 2014

+ Removed some debug statements (sysout)
+ Check for existence of formdata configuration to make the 0.2.2 release work with CMS 7.7. (thanks to Bert Leunis)

### Version 0.2.1
Release date: 20 Feb 2014

+ Removed some hardcoded labels
+ Added more tooltips
+ Documentation updates

### Version 0.2.0
Release date: 19 Feb 2014

+ Initial release see [features page](features.html) for more information.
