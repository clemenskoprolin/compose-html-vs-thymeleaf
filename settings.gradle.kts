pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://packages.jetbrains.team/maven/p/cmp/dev")
        google()
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
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

rootProject.name = "compose-html-vs-thymeleaf"

// The prototype consumes the local checkout so it can exercise composeHtmlToString
// before that API is available from a published Compose HTML artifact.
val composeHtmlCheckout = providers.gradleProperty("compose.html.checkout")
    .orElse("../compose-multiplatform/html")

includeBuild(composeHtmlCheckout.get()) {
    dependencySubstitution {
        substitute(module("org.jetbrains.compose.html:html-core"))
            .using(project(":html-core"))
    }
}

