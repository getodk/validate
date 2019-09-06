# ODK Validate
![Platform](https://img.shields.io/badge/platform-Java-blue.svg)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build status](https://circleci.com/gh/opendatakit/validate.svg?style=shield&circle-token=:circle-token)](https://circleci.com/gh/opendatakit/briefcase)
[![Slack status](http://slack.opendatakit.org/badge.svg)](http://slack.opendatakit.org)

ODK Validate is a desktop Java application for confirming that a form is compliant with the [ODK XForms spec](http://opendatakit.github.io/xforms-spec).
   

ODK Validate is part of Open Data Kit (ODK), a free and open-source set of tools which help organizations author, field, and manage mobile data collection solutions. Learn more about the Open Data Kit project and its history [here](https://opendatakit.org/about/) and read about example ODK deployments [here](https://opendatakit.org/about/deployments/).

* ODK website: [https://opendatakit.org](https://opendatakit.org)
* ODK Validate usage instructions: [https://opendatakit.org/use/validate](https://opendatakit.org/use/validate)
* ODK forum: [https://forum.opendatakit.org](https://forum.opendatakit.org)
* ODK developer Slack chat: [http://slack.opendatakit.org](http://slack.opendatakit.org) 
* ODK developer Slack archive: [http://opendatakit.slackarchive.io](http://opendatakit.slackarchive.io) 
* ODK developer wiki: [https://github.com/opendatakit/opendatakit/wiki](https://github.com/opendatakit/opendatakit/wiki)

## What validation is performed
Validate is a thin wrapper over the [JavaRosa](https://github.com/opendatakit/javarosa/) form parsing library. Given an XML document representing an [ODK XForms](https://opendatakit.github.io/xforms-spec/) form definition, Validate uses JavaRosa to first parse that form document into an in-memory representation and then it goes through that in-memory representation like a client such as [Collect](https://github.com/opendatakit/collect) would. This simulates displaying questions to the user and exercises the logic in the form. Errors detected by JavaRosa are presented to the user.

Validate does not simulate entering any data or perform static analysis on expressions so expressions that are not reached when first displaying a form are not verified. For example, if an `if` function call has a problematic call in one of its branches such as `if (/data/my_var = 'yes', fake-function('bad', 'bad'), 0)`, Validate will not identify that unless the default value for `my_var` is `yes`.

In general, issues with how the validation is done or what errors get presented are JavaRosa issues and should be filed [in its repository](https://github.com/opendatakit/javarosa/).

## Setting up your development environment

1. Fork the validate project ([why and how to fork](https://help.github.com/articles/fork-a-repo/))

1. Clone your fork of the project locally. At the command line:

        git clone https://github.com/YOUR-GITHUB-USERNAME/validate

We recommend using [IntelliJ IDEA](https://www.jetbrains.com/idea/) for development. On the welcome screen, click `Import Project`, navigate to your validate folder, and select the `build.gradle` file. Use the defaults through the wizard. Once the project is imported, IntelliJ may ask you to update your remote maven repositories. Follow the instructions to do so. 

The main class is `org.opendatakit.validate.FormValidator`.
 
## Running the project
 
To run the project, go to the `View` menu, then `Tool Windows > Gradle`. `run` will be in `odk-validate > Tasks > application > run`. Double-click `run` to run the application. This Gradle task will now be the default action in your `Run` menu. 

You must use the Gradle task to run the application because there is a generated class (`BuildConfig`) that IntelliJ may not properly import and recognize.

To package a runnable jar, use the `jar` Gradle task.

## Integrating Validate with your Java app

1. Download the [latest ODKValidate jar](https://opendatakit.org/downloads/download-category/validate/) or build your own.

1. Add the ODKValidate jar to your classpath.

1. Create a custom `ErrorListener` by implementing the `org.opendatakit.validate.ErrorListener` interface.

1. Add the custom `ErrorListener` to the form validator and call any of the various `validate(...)` methods.

See example below:
```java
ErrorListener listener = new ErrorListener() {
    public void error(Object err) {
        // ...custom code to handle error message
        System.err.println(err);
    }

    public void error(Object err, Throwable t) {
         // ...custom code to handle error message and exception
        System.err.println("" + err + t);
    }

    public void info(Object msg) {
        // ...custom code
        System.out.println(msg);
    }
};

new FormValidator().setErrorListener(listener).validateText("<xform>...");

```

## Calling Validate via the command line interface

You can use Validate through the command line like this: `java -jar ODKValidate.jar [--fail-fast] path/to/xform.xml [FORM...]`.

An exit code of 0 means **Valid XForm** and 1 is **Invalid XForm**.

## Contributing code
Any and all contributions to the project are welcome. ODK Validate is used across the world primarily by organizations with a social purpose so you can have real impact!

If you're ready to contribute code, see [the contribution guide](CONTRIBUTING.md).

## Downloading builds
Per-commit debug builds can be found on [CircleCI](https://circleci.com/gh/opendatakit/validate). Login with your GitHub account, click the build you'd like, then find the JAR in the Artifacts tab under $CIRCLE_ARTIFACTS/libs.

Current and previous production builds can be found on the [ODK website](https://opendatakit.org/downloads/download-info/odk-validate/).
