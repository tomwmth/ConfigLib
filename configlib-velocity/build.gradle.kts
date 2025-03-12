plugins {
    `core-config`
    `plugins-config`
}

repositories {
    maven(url = "https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    val velocity = "com.velocitypowered:velocity-api:3.1.1"
    compileOnly(velocity)
    annotationProcessor(velocity)
}
