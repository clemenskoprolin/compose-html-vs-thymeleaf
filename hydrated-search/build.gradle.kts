plugins {
    kotlin("multiplatform")
    kotlin("plugin.spring")
    kotlin("plugin.serialization")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.example"
version = "0.1.0-SNAPSHOT"

val composeVersion: String = providers.gradleProperty("compose.version").get()
val generatedWebResources = layout.buildDirectory.dir("generated/web-resources")

kotlin {
    jvm()
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "search-client.js"
            }
        }
        binaries.executable()
    }
    jvmToolchain(21)

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
            implementation("org.jetbrains.compose.html:html-core:$composeVersion")
            implementation("org.jetbrains.compose.html:kotlinx-browser-common-subset:0.0.1-ssr-local")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")
        }

        jvmMain {
            resources.srcDir(rootProject.file("web-assets"))
            resources.srcDir(generatedWebResources)
            dependencies {
                implementation(project(":catalog"))
                implementation("org.springframework.boot:spring-boot-starter-web")
                implementation(kotlin("reflect"))
            }
        }

    }
}

val copyBrowserBundle = tasks.register<Sync>("copyBrowserBundle") {
    dependsOn(tasks.named("jsBrowserDistribution"))
    from(layout.buildDirectory.dir("dist/js/productionExecutable")) {
        include("search-client.js")
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
