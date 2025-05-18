package de.exlll.configlib;

/**
 * Allows for the bundling of various {@link ConfigurationProperties.Builder} settings.
 * <p>
 * This will usually involve adding custom serializers for various platform-specific types.
 */
public abstract class PlatformBundle {
    protected abstract void register(ConfigurationProperties.Builder<?> builder);
}
