plugins {
    `core-config`
    `plugins-config`
}

repositories {
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.github.waterfallmc:waterfall-api:1.20-R0.2-SNAPSHOT")
}
