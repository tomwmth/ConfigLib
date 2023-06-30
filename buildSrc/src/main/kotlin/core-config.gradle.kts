plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
    idea
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

dependencies {
    testFixturesApi("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testFixturesApi("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testFixturesApi("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    testFixturesApi("org.junit.platform:junit-platform-runner:1.9.2")
    testFixturesApi("org.junit.platform:junit-platform-suite-api:1.9.2")
    testFixturesApi("org.mockito:mockito-inline:5.1.1")
    testFixturesApi("org.mockito:mockito-junit-jupiter:5.1.1")
    testFixturesApi("org.hamcrest:hamcrest-all:1.3")
    testFixturesApi("com.google.jimfs:jimfs:1.2")
}

publishing {
    val moduleId = project.name.split("-")[1].lowercase()
    val publicationName = moduleId.replaceFirstChar(Char::titlecase)

    publications {
        register<MavenPublication>(publicationName) {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "alpine-cloud"
            url = uri("https://lib.alpn.cloud/alpine-public")
            credentials {
                username = System.getenv("ALPINE_MAVEN_NAME")
                password = System.getenv("ALPINE_MAVEN_SECRET")
            }
        }
    }
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) {
    skip()
}
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) {
    skip()
}
