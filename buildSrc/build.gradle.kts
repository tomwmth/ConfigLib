plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("gradle.plugin.com.github.johnrengelman:shadow:8.0.0")
}
