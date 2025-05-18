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

publishing {
    val moduleId = project.name.split("-")[1].lowercase()
    val publicationName = moduleId.replaceFirstChar(Char::titlecase)

    publications {
        register<MavenPublication>(publicationName) {
            from(components["java"])

            pom {

                name = "ConfigLib $publicationName"
                description = "A library for saving, loading and updating configuration files."
                url = "https://github.com/tomwmth/ConfigLib"

                groupId = "dev.tomwmth.configlib"

                developers {
                    developer {
                        name = "Exlll"
                        email = "exlll321@gmail.com"
                        url = "https://github.com/Exlll"
                    }

                    developer {
                        name = "tomwmth"
                        email = "tomwmth@pm.me"
                        url = "https://github.com/tomwmth"
                    }
                }

                licenses {
                    license {
                        name = "MIT License"
                        url = "https://opensource.org/license/mit"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/tomwmth/ConfigLib.git"
                    developerConnection = "scm:git:ssh://github.com:tomwmth/ConfigLib.git"
                    url = "https://github.com/tomwmth/ConfigLib/tree/master"
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
