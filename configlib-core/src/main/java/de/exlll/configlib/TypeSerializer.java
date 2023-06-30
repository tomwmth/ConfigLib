package de.exlll.configlib;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.exlll.configlib.Validator.requireNonNull;

abstract class TypeSerializer<T, E extends ConfigurationElement<?>> implements Serializer<T, Map<?, ?>> {
    protected final Class<T> type;
    protected final ConfigurationProperties properties;
    protected final NameFormatter formatter;
    protected final Map<String, Serializer<?, ?>> serializers;

    protected TypeSerializer(Class<T> type, ConfigurationProperties properties) {
        this.type = requireNonNull(type, "type");
        this.properties = requireNonNull(properties, "configuration properties");
        this.formatter = properties.getNameFormatter();
        this.serializers = buildSerializerMap();
        requireSerializableElements();
    }

    static <T> TypeSerializer<T, ?> newSerializerFor(
            Class<T> type,
            ConfigurationProperties properties
    ) {
        return new ConfigurationSerializer<>(type, properties);
    }

    Map<String, Serializer<?, ?>> buildSerializerMap() {
        final SerializerSelector selector = new SerializerSelector(properties);
        try {
            return elements().stream().collect(Collectors.toMap(
                    ConfigurationElement::name,
                    selector::select
            ));
        } catch (StackOverflowError error) {
            String msg = "Recursive type definitions are not supported.";
            throw new ConfigurationException(msg, error);
        }
    }

    @Override
    public final Map<?, ?> serialize(T configuration) {
        final Map<String, Object> result = new LinkedHashMap<>();

        for (final E element : elements()) {
            final Object elementValue = element.value(configuration);

            if ((elementValue == null) && !properties.outputNulls())
                continue;

            final Object serializedValue = serialize(element, elementValue);
            final String formattedName = formatter.format(element.name());
            result.put(formattedName, serializedValue);
        }

        return result;
    }

    protected final Object serialize(E element, Object value) {
        // The following cast won't cause a ClassCastException because the serializers
        // are selected based on the element type.
        @SuppressWarnings("unchecked")
        final Serializer<Object, Object> serializer = (Serializer<Object, Object>)
                serializers.get(element.name());
        return (value != null) ? serializer.serialize(value) : null;
    }

    protected final Object deserialize(E element, Object value) {
        // This unchecked cast leads to an exception if the type of the object which
        // is deserialized is not a subtype of the type the deserializer expects.
        @SuppressWarnings("unchecked")
        final Serializer<Object, Object> serializer = (Serializer<Object, Object>)
                serializers.get(element.name());

        final Object deserialized;
        try {
            deserialized = serializer.deserialize(value);
        } catch (ClassCastException e) {
            String msg = baseDeserializeExceptionMessage(element, value) + "\n" +
                         "The type of the object to be deserialized does not " +
                         "match the type the deserializer expects.";
            throw new ConfigurationException(msg, e);
        } catch (RuntimeException e) {
            String msg = baseDeserializeExceptionMessage(element, value);
            throw new ConfigurationException(msg, e);
        }
        return deserialized;
    }

    protected abstract void requireSerializableElements();

    protected abstract String baseDeserializeExceptionMessage(E element, Object value);

    protected abstract List<E> elements();

    abstract T newDefaultInstance();
}
