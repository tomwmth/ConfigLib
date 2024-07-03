# ConfigLib

This is a fork of Exlll's ConfigLib that reintroduces Java 8 compatibility. Feature parity is not guaranteed, especially in the case of modern language features such as records.

For a proper README including important information and examples, please see the [upstream repository](https://github.com/Exlll/ConfigLib).

Builds are published to the [lib.alpn.cloud](https://lib.alpn.cloud/#/alpine-public) repository. The project can be added as a dependency like so:

```kotlin
repositories {
    maven("https://lib.alpn.cloud/alpine-public/")
}

dependencies {
    implementation("dev.tomwmth:configlib-spigot:4.5.0")
}
```
