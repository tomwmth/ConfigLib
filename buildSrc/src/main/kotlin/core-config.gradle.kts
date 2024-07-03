plugins {
    `java-library`
    `maven-publish`
    idea
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8

    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {

}

publishing {
    val moduleId = project.name.split("-")[1].lowercase()
    val publicationName = moduleId.replaceFirstChar(Char::titlecase)

    publications {
        register<MavenPublication>(publicationName) {
            from(components["java"])

            pom {

                name = "ConfigLib ${publicationName}"
                description = "A Minecraft library for saving, loading, updating, " +
                        "and commenting YAML configuration files."
                url = "https://github.com/Exlll/ConfigLib"

                developers {
                    developer {
                        name = "Exlll"
                        email = "exlll321@gmail.com"
                        url = "https://github.com/Exlll"
                    }
                }

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/license/mit"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/Exlll/ConfigLib.git"
                    developerConnection = "scm:git:ssh://github.com:Exlll/ConfigLib.git"
                    url = "https://github.com/Exlll/ConfigLib/tree/master"
                }
            }
        }
    }

    repositories {
        maven {
            name = "alpine"
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
