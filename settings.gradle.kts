pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/cmp/dev")
        google()
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        kotlin("multiplatform").version(extra["kotlin.version"] as String)
        kotlin("plugin.spring").version(extra["kotlin.version"] as String)
        kotlin("plugin.compose").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
        id("org.springframework.boot").version(extra["spring.boot.version"] as String)
        id("io.spring.dependency-management").version("1.1.7")
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/cmp/dev")
        google()
    }
}

rootProject.name = "compose-html-web-examples"

include(":catalog")
include(":hydrated-search")
include(":ssr-comparison")

val localProperties = java.util.Properties().apply {
    val propertiesFile = rootDir.resolve("local.properties")
    if (propertiesFile.isFile) propertiesFile.inputStream().use(::load)
}

val composeHtmlCheckout = providers.gradleProperty("compose.html.checkout").orNull
    ?: localProperties.getProperty("compose.html.checkout")

composeHtmlCheckout?.let { checkoutPath ->
    val checkoutDirectory = file(checkoutPath)
    require(checkoutDirectory.resolve("settings.gradle.kts").isFile) {
        "compose.html.checkout must point to a Compose HTML Gradle build: $checkoutDirectory"
    }
    includeBuild(checkoutDirectory) {
        dependencySubstitution {
            substitute(module("org.jetbrains.compose.html.eap:html-core-eap"))
                .using(project(":html-core-eap"))
        }
    }
}
