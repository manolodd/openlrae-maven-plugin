<div align="center"><img src="https://raw.githubusercontent.com/manolodd/openlrae-maven-plugin/development/src/main/resources/com/manolodominguez/openlraemp/logo/logo_openlraemp.png" alt="OpenLRAE Maven Plugin logo" width="400"/></div>

# PROJECT STATUS

## Master branch

[![Build Status](https://img.shields.io/travis/com/manolodd/openlrae-maven-plugin/master)](https://app.travis-ci.com/github/manolodd/openlrae-maven-plugin/builds)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=manolodd_openlrae-maven-plugin&branch=master&metric=alert_status)](https://sonarcloud.io/dashboard?branch=master&id=manolodd_openlrae-maven-plugin)
[![Maven Central](https://img.shields.io/maven-central/v/com.manolodominguez/openlrae-maven-plugin)](https://search.maven.org/artifact/com.manolodominguez/openlrae-maven-plugin/2.3/jar)

## Develop branch

[![Build Status](https://img.shields.io/travis/com/manolodd/openlrae-maven-plugin/development)](https://app.travis-ci.com/github/manolodd/openlrae-maven-plugin/builds)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=manolodd_openlrae-maven-plugin&branch=development&metric=alert_status)](https://sonarcloud.io/dashboard?branch=development&id=manolodd_openlrae-maven-plugin)
[![Maven Central](https://img.shields.io/badge/maven--central-Not%20available-inactive)](#)

# THE PROJECT

<b>OpenLRAE Maven Plugin</b> is maven plugin that allows you to analize licensing risks of your project's bill of component. It uses [OpenLRAE](https://openlrae.manolodominguez.com) as risk analysis engine, supporting most of its features.

This plugin adds a new <b>analyse</b> goal that is bound to the <b>compile</b> phase of Maven by default.

# LICENSE

## Latest snapshot version being developed:
 
- <b>OpenLRAE Maven Plugin 2.4-SNAPSHOT</b> (development branch) - Apache-2.0.

## Binary releases:

- <b>OpenLRAE Maven Plugin 2.3</b> (current, master branch) - Apache-2.0.
- <b>OpenLRAE Maven Plugin from 0.8 to 2.2</b> - Apache-2.0.

# PEOPLE BEHIND OPENLRAE

## Author:
    
 - Manuel Domínguez-Dorado - <ingeniero@ManoloDominguez.com>
   
# ARTIFACTS AVAILABILITY

You can download latest compiled stable releases from the releases section of this repository. Also, since release 0.8 OpenLRAE Maven Plugin is in Maven Central so you can add it as a plugin dependecy in your Maven project inserting the following in your pom.xml's plugins section:
```console
<plugin>
    <groupId>com.manolodominguez</groupId>
    <artifactId>openlrae-maven-plugin</artifactId>
    <version>2.3</version>
</plugin>
```
There are options to configure you can see in HOW TO USE THE PLUGIN section.

# COMPILING FROM SOURCES

If you want to test new features (please, do it and give feedback), you will need to compile the project from the current snapshot being developed. Follow these steps:

Clone the OpenLRAE Maven Plugin repo: 
```console
git clone https://github.com/manolodd/openlrae-maven-plugin.git
```
Choose the "development" branch, compile the code and obtain a binary jar including all you need (you will need to install Maven before):
```console
cd openlrae-maven-plugin
git checkout development
mvn package
```
The jar file will be located in "target" directory.
```console
cd target
```
Now, pick the plugin. It is:
```console
openlrae-maven-plugin-{YourVersion}.jar
```

# USING THE PLUGIN

OpenLRAE Maven Plugin is quite simple to use. You'll need two things:

1. A file containing the project description that is compliant with the [JSON Schema for project definitions](https://raw.githubusercontent.com/manolodd/openlrae/master/src/main/resources/com/manolodominguez/openlrae/json/OpenLRAEJSONSchemaForProjects.json) of OpenLRAE. Put this file wherever you want in your repo. The root folder (When the pom.xml file is) is recommended.
2. Adding a plugin dependency in your pom.xml and configuring some parameters to tune the analysis for your needs.

Here is a detailed and sefl-explanatory example of what you need to put in your pom.xml:

```console
<plugin>
    <groupId>com.manolodominguez</groupId>
    <artifactId>openlrae-maven-plugin</artifactId>
    <version>2.3</version>
    <executions>
        <execution>
            <goals>
                <goal>analyse</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <!-- 
        If not specified, the plugin will look for project.json file
        in the root of the repository (where the pom.xml file is). 
        This is a project definition file following the JSON Schema 
        for project definition of OpenLRAE.
        The value of this property has to be a file name or a file
        name with a path. e.g. filename.json, ./folder/filename.json
        and so on.
        -->
        <projectDefinitionFile>liric.json</projectDefinitionFile>
        <!-- 
        Available risks are those supported by the current version 
        of openlrae-maven-plugin (the same version of OpenLRAE). For 
        each one, you can set a threshold in order for the build to
        fail if at least one of them are exceeded.
        a) If you dont add a risk threshold, the plugin won't 
        analyze this risk.
        b) If you add a risk threshold, the plugin will analyze this
        risk and the build will fail if the computed risk exceeds
        the value of the threshold.
        c) If you add a risk threshold with value 1, the plugin will 
        analyze this but the build won't fail if the computed 
        risk exceeds the value of the threshold (in fact it is
        not possible to exceed a threshold of value 1 because 
        all OpenLRAE risk analysis values are between 0.0 and 1.0)
        The value of each threshold has to be a decimal number 
        between 0.0 and 1.0, for 0% and 1%. e.g. 0.1, 0.25, 1, 0...
        -->
        <riskThresholds>
            <HAVING_COMPONENTS_LICENSES_INCOMPATIBLE_WITH_PROJECT_LICENSES>0</HAVING_COMPONENTS_LICENSES_INCOMPATIBLE_WITH_PROJECT_LICENSES>
            <HAVING_A_LIMITED_SET_OF_POTENTIAL_PROJECT_LICENSES>1.0</HAVING_A_LIMITED_SET_OF_POTENTIAL_PROJECT_LICENSES>
            <HAVING_A_LIMITED_SET_OF_POTENTIAL_COMPONENTS_LICENSES>1.0</HAVING_A_LIMITED_SET_OF_POTENTIAL_COMPONENTS_LICENSES>
            <HAVING_OBSOLETE_PROJECT_LICENSES>1.0</HAVING_OBSOLETE_PROJECT_LICENSES>
            <HAVING_OBSOLETE_COMPONENTS_LICENSES>1.0</HAVING_OBSOLETE_COMPONENTS_LICENSES>
            <HAVING_UNFASHIONABLE_PROJECT_LICENSES>1.0</HAVING_UNFASHIONABLE_PROJECT_LICENSES>
            <HAVING_UNFASHIONABLE_COMPONENTS_LICENSES>1.0</HAVING_UNFASHIONABLE_COMPONENTS_LICENSES>
            <HAVING_SCARCELY_SPREAD_PROJECT_LICENSES>1.0</HAVING_SCARCELY_SPREAD_PROJECT_LICENSES>
            <HAVING_SCARCELY_SPREAD_COMPONENTS_LICENSES>1.0</HAVING_SCARCELY_SPREAD_COMPONENTS_LICENSES>
            <HAVING_HETEROGENEOUS_COMPONENTS_LICENSES>1.0</HAVING_HETEROGENEOUS_COMPONENTS_LICENSES>
            <HAVING_COMPONENTS_LICENSES_MISALIGNED_FROM_PROJECT_LICENSES>1.0</HAVING_COMPONENTS_LICENSES_MISALIGNED_FROM_PROJECT_LICENSES>
        </riskThresholds>
        <!-- 
        If set to true, a risks analysis report in JSON format will
        be saved in "target" directory as 
        report-YYYYMMAA-HHMMSS{projectDefinitionFile}. i.e. if 
        projectDefinitionFile has been set as "project.json", the
        report could be "report-20201225-160222-project.json" in 
        "target" directory. If undefined, or set to a value distinct
        of true, risk analysis report won't be saved.
        -->
        <saveReport>true</saveReport>
        <!-- 
        If set to true, a risks analysis report in JSON format will
        be included in the build log. If undefined, or set to a 
        value distinct of true, risk analysis report won't be logged.
        -->
        <showReport>true</showReport>
        <!-- 
        This is used only if saveReport or showReport are set to 
        true. If this is the case, you can choose the language used 
        to generate the report. If the specified Locale is not 
        supported by OpenLRAE or it is an incorrect value, then the 
        default language (English) is used.
        The value of this property should be a valid Locale tag. e.g. 
        es, es-ES, en-US, ja-JP-x-lvariant-JP, etc.
        -->
        <reportLanguage>es</reportLanguage>
    </configuration>
</plugin>    
```
Of course, this is a complete example. A more usual one could be:
```console
<plugin>
    <groupId>com.manolodominguez</groupId>
    <artifactId>openlrae-maven-plugin</artifactId>
    <version>2.3</version>
    <executions>
        <execution>
            <goals>
                <goal>analyse</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <riskThresholds>
            <HAVING_COMPONENTS_LICENSES_INCOMPATIBLE_WITH_PROJECT_LICENSES>0</HAVING_COMPONENTS_LICENSES_INCOMPATIBLE_WITH_PROJECT_LICENSES>
            <HAVING_COMPONENTS_LICENSES_MISALIGNED_FROM_PROJECT_LICENSES>0.5</HAVING_COMPONENTS_LICENSES_MISALIGNED_FROM_PROJECT_LICENSES>
        </riskThresholds>
        <showReport>true</showReport>
    </configuration>
</plugin>    
```
In this example, openlrae-maven-plugin will load the project definition from a 'project.json' file (the default value) that has to be located at the root of the repo. And will analyse two licensing risk:

1. HAVING_COMPONENTS_LICENSES_INCOMPATIBLE_WITH_PROJECT_LICENSES, whose risk value should not exceed 0.
2. HAVING_COMPONENTS_LICENSES_MISALIGNED_FROM_PROJECT_LICENSES, whose risk value should not exceed 0.5.

If any of these thresholds are exceeded, the build fails.

Also, a risks analysis report will be generated in English (the default value) and will be dumped to the log of the build.

# Current features

## Supported risks

OpenLRAE Maven Plugin is based on the same version of OpenLRAE. The features supported by the plugin are the same than those supported by OpenLRAE. So, read [OpenLRAE Features](https://github.com/manolodd/openlrae) to know more.

# THIRD-PARTY COMPONENTS

OpenLRAE Maven Plugin uses third-party components each one of them having its own OSS license. License compatibility has been taken into account to allow OpenLRAE Maven Plugin to be released under its current OSS licence. They are:

- mjson 1.4.1 - Apache-2.0 - http://bolerio.github.io/mjson
- junit-jupiter-engine 5.8.7 - EPL-2.0 - https://junit.org/junit5
- openlrae-2.2 - LGPL-3.0-or-later - https://openlrae.manolodominguez.com

Thanks folks!

# WHAT CAN YOU CONTRIBUTE?

The best way of contributing to this plugin is contributing to the library it is based on. So, take a look at [OpenLRAE project](https://github.com/manolodd/openlrae).

#### Thanks for contributing.
