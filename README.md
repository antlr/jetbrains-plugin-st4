# IntelliJ Plugin for StringTemplate v4 [![Build Status](https://travis-ci.org/antlr/jetbrains-plugin-st4.svg?branch=master)](https://travis-ci.org/antlr/jetbrains-plugin-st4) [![Latest version](https://img.shields.io/jetbrains/plugin/v/8041.svg?label=latest%20version)](https://plugins.jetbrains.com/plugin/8041-stringtemplate-v4-plugin) ![Downloads](https://img.shields.io/jetbrains/plugin/d/8041.svg)

A plugin that adds support for StringTemplate v4 to [IntelliJ](https://www.jetbrains.com/idea/)-based IDEs (version 15.x and later).

It understands `.stg` and `.st` files. For example,

<img src="images/structview.png" width=350>

<img src="images/darcula.png" width=350>

See the [plugin page](https://plugins.jetbrains.com/plugin/8041?pr=) for more information.

# Highlighting the target language

You can configure `Template Data Languages` to make the editor highlight the content around StringTemplate tags.
For exemple, if your template is suppose to generate Java code, you can go to `File | Settings | Languages & Frameworks | Template Data Languages` 
and configure which language to highlight:

<img src="images/template-data-language.png" width=350>

The editor will now highlight Java parts around ST tags:

<img src="images/java-highlight.png" width=350>

# Building

To build the plugin:
`gradlew buildPlugin`

To run the plugin:
`gradlew runIde`
