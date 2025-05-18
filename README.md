# ConfigLib
This is a fork of [Exlll's ConfigLib](https://github.com/Exlll/ConfigLib).

For a proper README please see the [upstream repository](https://github.com/Exlll/ConfigLib/blob/master/README.md). Most of the information there will apply. This README will focus on the differences.

## Differences
- Restored Java 8 compatibility.
- The `final` modifier does not exclude a field from being treated as a configuration value.
- Any type will work as a map key provided there is a serializer that converts it to a simple type.
- No longer functions as a plugin. The library is now intended to be shaded and only provides platform-specific artifacts for the purpose of extra default serializers.

## Module Breakdown
- `configlib-core`: Provides all the core functionality of the library. It is unlikely you will need to directly depend on this.
- `configlib-yaml`: Provides a YAML backend. You will need to depend on this if you wish to use YML as a storage format.
- `configlib-bukkit`: Provides a platform bundle that implements some helpful serializers specific to the Bukkit API.

## Dependency Usage
Builds are published to the [lib.alpn.cloud](https://lib.alpn.cloud/#/alpine-public) repository. The project can be added as a dependency like so:

### Gradle (Kotlin DSL)
```kotlin
repositories {
    maven("https://lib.alpn.cloud/alpine-public/")
}

dependencies {
    implementation("dev.tomwmth.configlib:configlib-yaml:4.6.0")
}
```

### Gradle (Groovy DSL)
```groovy
repositories {
    maven { url 'https://lib.alpn.cloud/alpine-public/' }
}

dependencies {
    implementation 'dev.tomwmth.configlib:configlib-yaml:4.6.0'
}
```

### Maven
```xml
<repository>
    <id>Alpine Cloud</id>
    <url>https://lib.alpn.cloud/alpine-public/</url>
</repository>

<dependency>
    <groupId>dev.tomwmth.configlib</groupId>
    <artifactId>configlib-yaml-</artifactId>
    <version>4.6.0</version>
</dependency>
```

## Basic Usage
The following is a simple example using the YAML backend and adding the Bukkit platform bundle.

```java
YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder()
        .inputNulls(true)
        .outputNulls(true)
        .charset(StandardCharsets.UTF_8)
        .addBundle(BukkitBundle.DEFAULT)
        .build();

Path path = ...;
ExampleConfig config = YamlConfigurations.update(path, ExampleConfig.class, properties);
```
