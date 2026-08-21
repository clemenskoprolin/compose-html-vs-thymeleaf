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
    implementation("org.jetbrains.compose.runtime:runtime:$composeVersion")
    implementation("org.jetbrains.compose.html:html-core:$composeVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation(kotlin("reflect"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
