package de.exlll.configlib;

import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

final class ConfigurationSerializableSerializer implements Serializer<ConfigurationSerializable, String> {
    static ConfigurationSerializableSerializer DEFAULT =
            new ConfigurationSerializableSerializer();

    private final Yaml yaml;

    ConfigurationSerializableSerializer() {
        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yaml = new Yaml(new YamlConstructor(), new YamlRepresenter(), options);
    }

    @Override
    public String serialize(ConfigurationSerializable element) {
        return yaml.dump(element);
    }

    @Override
    public ConfigurationSerializable deserialize(String element) {
        return (ConfigurationSerializable) yaml.load(element);
    }
}
