plugins {
    `core-config`
    `plugins-config`
}

repositories {
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
}
