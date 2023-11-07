plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.16.0"
    id("antlr")
}

group = "antlr"
version = properties["pluginVersion"].toString()

repositories {
    mavenCentral()
}

intellij {
    version.set(properties["ideaVersion"].toString())
    type.set("IC") // Target IDE Platform

    pluginName.set("jetbrains-plugin-st4")
    downloadSources = true
    updateSinceUntilBuild = false
}

dependencies {
    antlr("org.antlr:antlr4:${properties["antlr4Version"]}") {
        exclude(group = "com.ibm.icu", module = "icu4j")
    }
    implementation("org.antlr:antlr4-intellij-adaptor:0.1")
    implementation("org.antlr:antlr4-runtime:${properties["antlr4Version"]}")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.5.0")
}

sourceSets {
    main {
        antlr {
            setIncludes(listOf("ST*.g4"))
        }
    }
}

// See https://github.com/gradle/gradle/issues/820#issuecomment-1585814999
configurations {
    api {
        setExtendsFrom(extendsFrom.filterNot { it == antlr.get() })
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    patchPluginXml {
        sinceBuild.set("222.4554.10")
    }

    generateGrammarSource {
        arguments = arguments + listOf(
            "-package", "org.antlr.jetbrains.st4plugin.parsing",
            "-long-messages",
            "-no-visitor",
            "-no-listener",
            "-lib","src/main/antlr"
        )
    }
}
