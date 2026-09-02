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

kotlin {
    jvmToolchain(21)
    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

dependencies {
    implementation(project(":catalog"))
    implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
    implementation("org.jetbrains.compose.html:html-core:$composeVersion")
    implementation("org.jetbrains.compose.html:kotlinx-browser-common-subset:0.0.1-ssr-local")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation(kotlin("reflect"))

    // The Compose renderer replays the catalog module's sanitized README markup.
    implementation("org.jsoup:jsoup:1.23.1")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
