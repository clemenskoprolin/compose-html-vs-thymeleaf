import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    kotlin("jvm")
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

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

dependencies {
    implementation(project(":catalog"))
    implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
    runtimeOnly("androidx.compose.runtime:runtime-desktop:$composeVersion")
    implementation("org.jetbrains.compose.html.eap:html-core-eap:$composeHtmlEapVersion")
    implementation("org.jetbrains.compose.html:kotlinx-browser-common-subset:$kotlinxBrowserCommonSubsetVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation(kotlin("reflect"))

    // The Compose renderer replays the catalog module's sanitized README markup.
    implementation("org.jsoup:jsoup:1.23.1")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// The JetBrains runtime desktop artifact is an empty metadata shim with the same jar name
// as the AndroidX runtime. Keep the implementation jar when Spring Boot packages BOOT-INF/lib.
configurations.named("runtimeClasspath") {
    exclude(group = "org.jetbrains.compose.runtime", module = "runtime-desktop")
}

sourceSets.main {
    resources.srcDir(rootProject.file("web-assets"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.named<BootRun>("bootRun") {
    sourceResources(sourceSets["main"])
}
