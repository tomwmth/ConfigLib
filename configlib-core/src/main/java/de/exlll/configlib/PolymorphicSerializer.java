package de.exlll.configlib;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

final class PolymorphicSerializer implements Serializer<Object, Map<?, ?>> {
    private final SerializerContext context;
    private final Class<?> polymorphicType;
    private final Polymorphic polymorphic;
    private final Map<String, Class<?>> typeByAlias = new HashMap<>();
    private final Map<Class<?>, String> aliasByType = new HashMap<>();

    public PolymorphicSerializer(SerializerContext context) {
        this.context = context;
        // we know it's a class because of SerializerSelector#findMetaSerializerOnType
        this.polymorphicType = (Class<?>) context.annotatedType().getType();
        this.polymorphic = polymorphicType.getAnnotation(Polymorphic.class);
        requireNonBlankProperty();
        initAliases();
    }

    private void requireNonBlankProperty() {
        if (polymorphic.property().isEmpty()) {
            String msg = String.format("The @Polymorphic annotation does not allow a blank property name but " +
                    "type '%s' uses one.", polymorphicType.getName());
            throw new ConfigurationException(msg);
        }
    }

    private void initAliases() {
        final PolymorphicTypes polymorphicTypes = polymorphicType.getAnnotation(PolymorphicTypes.class);

        if (polymorphicTypes == null)
            return;

        for (PolymorphicTypes.Type pType : polymorphicTypes.value()) {
            final Class<?> type = pType.type();
            final String alias = pType.alias().isEmpty() ? type.getName() : pType.alias();

            requireDistinctAliases(alias);
            requireDistinctTypes(type);

            typeByAlias.put(alias, type);
            aliasByType.put(type, alias);
        }
    }

    private void requireDistinctAliases(String alias) {
        if (typeByAlias.containsKey(alias)) {
            String msg = String.format("The @PolymorphicTypes annotation must not use the same alias for multiple types. Alias '%s' appears more than once.",
                            alias);
            throw new ConfigurationException(msg);
        }
    }

    private void requireDistinctTypes(Class<?> type) {
        if (aliasByType.containsKey(type)) {
            String msg = String.format("The @PolymorphicTypes annotation must not contain multiple definitions for the same subtype. Type '%s' appears more than once.",
                    type.getName());
            throw new ConfigurationException(msg);
        }
    }

    @Override
    public Map<?, ?> serialize(Object element) {
        final Class<?> elementType = element.getClass();
        // this cast won't cause any exceptions as we only pass objects of types the
        // serializer expects
        @SuppressWarnings("unchecked")
        final TypeSerializer<Object, ?> serializer = (TypeSerializer<Object, ?>) TypeSerializer.newSerializerFor(
                elementType,
                context.properties()
        );
        final Map<?, ?> serialization = serializer.serialize(element);

        requireSerializationNotContainsProperty(serialization);

        final Map<String, String> result = new LinkedHashMap<>();
        result.put(polymorphic.property(), getTypeIdentifierByType(elementType));
        result.putAll((Map<String, String>) serialization);
        return result;
    }

    private String getTypeIdentifierByType(Class<?> type) {
        final String alias = aliasByType.get(type);
        return (alias == null) ? type.getName() : alias;
    }

    private void requireSerializationNotContainsProperty(Map<?, ?> serialization) {
        if (serialization.containsKey(polymorphic.property())) {
            String msg = String.format("Polymorphic serialization for type '%s' failed. The type contains a " +
                    "configuration element with name '%s' but that name is " +
                    "used by the @Polymorphic property.", polymorphicType.getName(), polymorphic.property());
            throw new ConfigurationException(msg);
        }
    }

    @Override
    public Object deserialize(Map<?, ?> element) {
        requirePropertyPresent(element);

        final Object typeIdentifier = element.get(polymorphic.property());
        requireTypeIdentifierString(typeIdentifier);

        final Class<?> type = getTypeByTypeIdentifier((String) typeIdentifier);
        final TypeSerializer<?, ?> serializer = TypeSerializer.newSerializerFor(type, context.properties());
        return serializer.deserialize(element);
    }

    private Class<?> getTypeByTypeIdentifier(String typeIdentifier) {
        final Class<?> type = typeByAlias.get(typeIdentifier);
        return (type == null) ? tryFindClass(typeIdentifier) : type;
    }

    private Class<?> tryFindClass(String className) {
        try {
            return Reflect.getClassByName(className);
        } catch (RuntimeException e) {
            String msg = String.format("Polymorphic deserialization for type '%s' failed. The class '%s' does not exist.",
                    polymorphicType.getName(), className);
            throw new ConfigurationException(msg, e);
        }
    }

    private void requirePropertyPresent(Map<?, ?> element) {
        if (element.get(polymorphic.property()) != null)
            return;
        String msg = String.format("Polymorphic deserialization for type '%s' failed.\n" +
                "The property '%s' which holds the type is missing.\n" +
                "Value to be deserialized:\n" +
                "%s", polymorphicType.getName(), polymorphic.property(), element);
        throw new ConfigurationException(msg);
    }

    private void requireTypeIdentifierString(Object typeIdentifier) {
        if (typeIdentifier instanceof String)
            return;
        String msg = String.format("Polymorphic deserialization for type '%s' failed. The type identifier '%s' " +
                "which should hold the type is not a string but of type '%s'.",
                polymorphicType.getName(), typeIdentifier, typeIdentifier.getClass().getName());
        throw new ConfigurationException(msg);
    }

    Class<?> getPolymorphicType() {
        return polymorphicType;
    }
}
