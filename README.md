# ODK Validate
![Platform](https://img.shields.io/badge/platform-Java-blue.svg)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build status](https://circleci.com/gh/opendatakit/validate.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/opendatakit/briefcase)
[![Slack status](http://slack.opendatakit.org/badge.svg)](http://slack.opendatakit.org)

ODK Validate is a desktop Java application for confirming that a form is compliant with the [ODK XForms spec](http://opendatakit.github.io/xforms-spec).
   

ODK Validate is part of Open Data Kit (ODK), a free and open-source set of tools which help organizations author, field, and manage mobile data collection solutions. Learn more about the Open Data Kit project and its history [here](https://opendatakit.org/about/) and read about example ODK deployments [here](https://opendatakit.org/about/deployments/).

* ODK website: [https://opendatakit.org](https://opendatakit.org)
* ODK Validate usage instructions: [https://opendatakit.org/use/validate](https://opendatakit.org/use/validate)
* ODK community mailing list: [http://groups.google.com/group/opendatakit](http://groups.google.com/group/opendatakit)
* ODK developer mailing list: [http://groups.google.com/group/opendatakit-developers](http://groups.google.com/group/opendatakit-developers)
* ODK developer Slack chat: [http://slack.opendatakit.org](http://slack.opendatakit.org) 
* ODK developer Slack archive: [http://opendatakit.slackarchive.io](http://opendatakit.slackarchive.io) 
* ODK developer wiki: [https://github.com/opendatakit/opendatakit/wiki](https://github.com/opendatakit/opendatakit/wiki)

## Setting up your development environment

1. Fork the validate project ([why and how to fork](https://help.github.com/articles/fork-a-repo/))

1. Clone your fork of the project locally. At the command line:

        git clone https://github.com/YOUR-GITHUB-USERNAME/validate

We recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/) for development. On the welcome screen, click `Import Project`, navigate to your validate folder, and select the `build.gradle` file. Use the defaults through the wizard. Once the project is imported, IntelliJ may ask you to update your remote maven repositories. Follow the instructions to do so. 

The main class is `org.opendatakit.validate.FormValidator`.
 
## Running the project
 
To run the project, use the `run` Gradle task. To package a runnable jar, use the `jar` Gradle task.

## Contributing code
Any and all contributions to the project are welcome. ODK Validate is used across the world primarily by organizations with a social purpose so you can have real impact!

If you're ready to contribute code, see [the contribution guide](CONTRIBUTING.md).

## Downloading builds
Per-commit debug builds can be found on [CircleCI](https://circleci.com/gh/opendatakit/validate). Login with your GitHub account, click the build you'd like, then find the JAR in the Artifacts tab under $CIRCLE_ARTIFACTS/libs.

Current and previous production builds can be found on the [ODK website](https://opendatakit.org/downloads/download-info/odk-validate/).
