package de.exlll.configlib;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * A bundle that adds a default serializer for {@link ConfigurationSerializable}.
 * <p>
 * This enables serialization of many Bukkit classes like {@link org.bukkit.inventory.ItemStack},
 * {@link org.bukkit.util.Vector} and {@link org.bukkit.potion.PotionEffect}.
 */
public final class BukkitBundle extends PlatformBundle {
    public static final BukkitBundle DEFAULT = new BukkitBundle();

    @Override
    protected void register(ConfigurationProperties.Builder<?> builder) {
        builder.addSerializerByCondition(
                type -> type instanceof Class<?> && ConfigurationSerializable.class.isAssignableFrom((Class<?>) type),
                ConfigurationSerializableSerializer.DEFAULT
        );
    }
}
