plugins {
    `java-library`
}

dependencies {
    api(project(":configlib-core"))
}

tasks.compileJava {
    dependsOn(project(":configlib-core").tasks.check)
}