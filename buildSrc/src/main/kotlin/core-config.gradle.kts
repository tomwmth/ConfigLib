plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
    idea
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withJavadocJar()
    withSourcesJar()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
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
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Exlll/ConfigLib")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    val moduleId = project.name.split("-")[1].lowercase()
    val publicationName = moduleId.replaceFirstChar(Char::titlecase)

    publications {
        register<MavenPublication>(publicationName) {
            from(components["java"])
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
