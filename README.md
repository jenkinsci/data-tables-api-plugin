# jQuery DataTables Jenkins Plugin

[![Jenkins Version](https://img.shields.io/badge/Jenkins-2.138.4-green.svg?label=min.%20Jenkins)](https://jenkins.io/download/)
![JDK8](https://img.shields.io/badge/jdk-8-yellow.svg?label=min.%20JDK)
[![License: MIT](https://img.shields.io/badge/license-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Provides [jQuery DataTables](https://datatables.net) for Jenkins Plugins.

This plugin contains the latest [WebJars](https://www.webjars.org) release and corresponding Jenkins UI elements. 

## How to use the plugin

In order to use this JS library, add a maven dependency to your pom:
```xml
<dependency>
  <groupId>io.jenkins.plugins</groupId>
  <artifactId>data-tables-api</artifactId>
  <version>[latest version]</version>
</dependency>
```

Then you can use DataTables in your jelly files using the following snippet:
```xml
<st:adjunct includes="io.jenkins.plugins.data-tables"/>
```
 
You can find several examples of Jenkins views that use DataTables in the 
[Warnings Next Generation plugin](https://github.com/jenkinsci/warnings-ng-plugin).

[![Jenkins](https://ci.jenkins.io/job/Plugins/job/data-tables-api-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/data-tables-api-plugin/job/master/)
[![GitHub Actions](https://github.com/jenkinsci/data-tables-api-plugin/workflows/GitHub%20Actions/badge.svg)](https://github.com/jenkinsci/data-tables-api-plugin/actions)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/26cf15808a764bc3a611cfeaa915d883)](https://www.codacy.com/manual/uhafner/data-tables-api-plugin?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jenkinsci/data-tables-api-plugin&amp;utm_campaign=Badge_Grade)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/jenkinsci/data-tables-api-plugin.svg)](https://github.com/jenkinsci/data-tables-api-plugin/pulls)

