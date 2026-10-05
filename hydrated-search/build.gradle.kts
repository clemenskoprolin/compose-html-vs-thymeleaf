@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    kotlin("multiplatform")
    kotlin("plugin.spring")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.example"
version = "0.1.0-SNAPSHOT"

val composeVersion: String = providers.gradleProperty("compose.version").get()
val composeHtmlEapVersion: String = providers.gradleProperty("compose.html.eap.version").get()
val kotlinxBrowserCommonSubsetVersion: String =
    providers.gradleProperty("compose.html.eap.kotlinx-browser-common-subset.version").get()
val generatedWebResources = layout.buildDirectory.dir("generated/web-resources")

kotlin {
    jvm()
    wasmJs {
        outputModuleName = "search-client"
        browser()
        binaries.executable()
    }
    jvmToolchain(21)

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
            implementation("org.jetbrains.compose.html.eap:html-core-eap:$composeHtmlEapVersion")
            implementation("org.jetbrains.compose.html:kotlinx-browser-common-subset:$kotlinxBrowserCommonSubsetVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
        }

        jvmMain {
            resources.srcDir(rootProject.file("web-assets"))
            resources.srcDir(generatedWebResources)
            dependencies {
                implementation(project(":catalog"))
                implementation("org.springframework.boot:spring-boot-starter-web")
                implementation("tools.jackson.core:jackson-databind")
                implementation(kotlin("reflect"))
            }
        }

    }
}

val copyBrowserBundle = tasks.register<Sync>("copyBrowserBundle") {
    dependsOn(tasks.named("wasmJsProductionExecutableCompileSync"))
    val browserDistribution = layout.buildDirectory.dir(
        "compileSync/wasmJs/main/productionExecutable/optimized",
    )
    from(browserDistribution) {
        include("search-client*.mjs", "search-client.wasm")
    }
    into(generatedWebResources.map { it.dir("static") })
}

tasks.named("jvmProcessResources") {
    dependsOn(copyBrowserBundle)
}

val jvmMainCompilation = kotlin.targets.getByName("jvm").compilations.getByName("main")

tasks.register<JavaExec>("bootRun") {
    group = "application"
    description = "Runs the hydrated-search Spring Boot application."
    dependsOn("jvmMainClasses")
    mainClass.set("com.example.htmlcomparison.hydration.ApplicationKt")
    classpath(jvmMainCompilation.output.allOutputs)
    classpath(jvmMainCompilation.runtimeDependencyFiles)
}
