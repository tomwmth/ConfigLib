package de.exlll.configlib;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * This class contains convenience methods for loading, saving, and updating configurations.
 */
public final class YamlConfigurations {
    private YamlConfigurations() {}

    /**
     * Loads a configuration of the given type from the specified YAML file using a
     * {@code YamlConfigurationProperties} object with default values.
     *
     * @param configurationFile the file the configuration is loaded from
     * @param configurationType the type of configuration
     * @param <T>               the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @throws ConfigurationException   if the configuration cannot be deserialized
     * @throws IllegalArgumentException if the file does not exist or is not a regular file
     * @throws NullPointerException     if any parameter is null
     * @throws RuntimeException         if reading the configuration throws an exception
     * @see YamlConfigurationStore#load(Path)
     */
    public static <T> T load(Path configurationFile, Class<T> configurationType) {
        final YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder().build();
        return load(configurationFile, configurationType, properties);
    }

    /**
     * Loads a configuration of the given type from the specified YAML file using a
     * {@code YamlConfigurationProperties} object that is built by a builder. The builder is
     * initialized with default values and can be configured by the {@code propertiesConfigurer}.
     *
     * @param configurationFile    the file the configuration is loaded from
     * @param configurationType    the type of configuration
     * @param propertiesConfigurer the consumer used to configure the builder
     * @param <T>                  the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @throws ConfigurationException   if the configuration cannot be deserialized
     * @throws IllegalArgumentException if the file does not exist or is not a regular file
     * @throws NullPointerException     if any parameter is null
     * @throws RuntimeException         if reading the configuration throws an exception
     * @see YamlConfigurationStore#load(Path)
     */
    public static <T> T load(
            Path configurationFile,
            Class<T> configurationType,
            Consumer<YamlConfigurationProperties.Builder<?>> propertiesConfigurer
    ) {
        final YamlConfigurationProperties.Builder<?> builder = YamlConfigurationProperties.newBuilder();
        propertiesConfigurer.accept(builder);
        return load(configurationFile, configurationType, builder.build());
    }

    /**
     * Loads a configuration of the given type from the specified YAML file using the given
     * {@code YamlConfigurationProperties} object.
     *
     * @param configurationFile the file the configuration is loaded from
     * @param configurationType the type of configuration
     * @param properties        the configuration properties
     * @param <T>               the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @throws ConfigurationException   if the configuration cannot be deserialized
     * @throws IllegalArgumentException if the file does not exist or is not a regular file
     * @throws NullPointerException     if any parameter is null
     * @throws RuntimeException         if reading the configuration throws an exception
     * @see YamlConfigurationStore#load(Path)
     */
    public static <T> T load(
            Path configurationFile,
            Class<T> configurationType,
            YamlConfigurationProperties properties
    ) {
        final YamlConfigurationStore<T> store = new YamlConfigurationStore<>(configurationType, properties);
        return store.load(configurationFile);
    }

    /**
     * Updates a YAML configuration file with a configuration of the given type using a
     * {@code YamlConfigurationProperties} object with default values.
     * <p>
     * See {@link YamlConfigurationStore#update(Path)} for an explanation of how the update is done.
     *
     * @param configurationFile the configuration file that is updated
     * @param configurationType the type of configuration
     * @param <T>               the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @throws ConfigurationException if the configuration cannot be deserialized
     * @throws NullPointerException   if any argument is null
     * @throws RuntimeException       if loading or saving the configuration throws an exception
     * @see YamlConfigurationStore#update(Path)
     */
    public static <T> T update(Path configurationFile, Class<T> configurationType) {
        final YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder().build();
        return update(configurationFile, configurationType, properties);
    }

    /**
     * Updates a YAML configuration file with a configuration of the given type using a
     * {@code YamlConfigurationProperties} object that is built by a builder. The builder is
     * initialized with default values and can be configured by the {@code propertiesConfigurer}.
     * <p>
     * See {@link YamlConfigurationStore#update(Path)} for an explanation of how the update is done.
     *
     * @param configurationFile    the configuration file that is updated
     * @param configurationType    the type of configuration
     * @param propertiesConfigurer the consumer used to configure the builder
     * @param <T>                  the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @throws ConfigurationException if the configuration cannot be deserialized
     * @throws NullPointerException   if any argument is null
     * @throws RuntimeException       if loading or saving the configuration throws an exception
     * @see YamlConfigurationStore#update(Path)
     */
    public static <T> T update(
            Path configurationFile,
            Class<T> configurationType,
            Consumer<YamlConfigurationProperties.Builder<?>> propertiesConfigurer
    ) {
        final YamlConfigurationProperties.Builder<?> builder = YamlConfigurationProperties.newBuilder();
        propertiesConfigurer.accept(builder);
        return update(configurationFile, configurationType, builder.build());
    }

    /**
     * Updates a YAML configuration file with a configuration of the given type using the given
     * {@code YamlConfigurationProperties} object.
     * <p>
     * See {@link YamlConfigurationStore#update(Path)} for an explanation of how the update is done.
     *
     * @param configurationFile the configuration file that is updated
     * @param configurationType the type of configuration
     * @param properties        the configuration properties
     * @param <T>               the configuration type
     * @return a newly created configuration initialized with values taken from the configuration file
     * @throws ConfigurationException if the configuration cannot be deserialized
     * @throws NullPointerException   if any argument is null
     * @throws RuntimeException       if loading or saving the configuration throws an exception
     * @see YamlConfigurationStore#update(Path)
     */
    public static <T> T update(
            Path configurationFile,
            Class<T> configurationType,
            YamlConfigurationProperties properties
    ) {
        final YamlConfigurationStore<T> store = new YamlConfigurationStore<>(configurationType, properties);
        return store.update(configurationFile);
    }

    /**
     * Saves a configuration of the given type to the specified YAML file using a
     * {@code YamlConfigurationProperties} object with default values.
     *
     * @param configuration     the configuration that is saved
     * @param configurationType the type of configuration
     * @param configurationFile the file the configuration is saved to
     * @param <T>               the configuration type
     * @throws ConfigurationException if the configuration contains invalid values or
     *                                cannot be serialized
     * @throws NullPointerException   if any argument is null
     * @throws RuntimeException       if writing the configuration throws an exception
     * @see YamlConfigurationStore#save(Object, Path)
     */
    public static <T> void save(
            Path configurationFile,
            Class<T> configurationType,
            T configuration
    ) {
        final YamlConfigurationProperties properties = YamlConfigurationProperties.newBuilder().build();
        save(configurationFile, configurationType, configuration, properties);
    }

    /**
     * Saves a configuration of the given type to the specified YAML file using a
     * {@code YamlConfigurationProperties} object that is built by a builder. The builder is
     * initialized with default values and can be configured by the {@code propertiesConfigurer}.
     *
     * @param configuration        the configuration that is saved
     * @param configurationType    the type of configuration
     * @param configurationFile    the file the configuration is saved to
     * @param propertiesConfigurer the consumer used to configure the builder
     * @param <T>                  the configuration type
     * @throws ConfigurationException if the configuration contains invalid values or
     *                                cannot be serialized
     * @throws NullPointerException   if any argument is null
     * @throws RuntimeException       if writing the configuration throws an exception
     * @see YamlConfigurationStore#save(Object, Path)
     */
    public static <T> void save(
            Path configurationFile,
            Class<T> configurationType,
            T configuration,
            Consumer<YamlConfigurationProperties.Builder<?>> propertiesConfigurer
    ) {
        final YamlConfigurationProperties.Builder<?> builder = YamlConfigurationProperties.newBuilder();
        propertiesConfigurer.accept(builder);
        save(configurationFile, configurationType, configuration, builder.build());
    }

    /**
     * Saves a configuration of the given type to the specified YAML file using the given
     * {@code YamlConfigurationProperties} object.
     *
     * @param configuration     the configuration that is saved
     * @param configurationType the type of configuration
     * @param configurationFile the file the configuration is saved to
     * @param properties        the configuration properties
     * @param <T>               the configuration type
     * @throws ConfigurationException if the configuration contains invalid values or
     *                                cannot be serialized
     * @throws NullPointerException   if any argument is null
     * @throws RuntimeException       if writing the configuration throws an exception
     * @see YamlConfigurationStore#save(Object, Path)
     */
    public static <T> void save(
            Path configurationFile,
            Class<T> configurationType,
            T configuration,
            YamlConfigurationProperties properties
    ) {
        final YamlConfigurationStore<T> store = new YamlConfigurationStore<>(configurationType, properties);
        store.save(configuration, configurationFile);
    }
}
