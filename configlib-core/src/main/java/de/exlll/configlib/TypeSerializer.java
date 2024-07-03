package de.exlll.configlib;

import de.exlll.configlib.ConfigurationElements.FieldElement;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static de.exlll.configlib.Validator.requireNonNull;

abstract class TypeSerializer<T, E extends ConfigurationElement<?>> implements Serializer<T, Map<?, ?>> {
    protected final Class<T> type;
    protected final ConfigurationProperties properties;
    protected final NameFormatter formatter;
    protected final Map<String, Serializer<?, ?>> serializers;
    protected final UnaryOperator<T> postProcessor;

    protected TypeSerializer(Class<T> type, ConfigurationProperties properties) {
        this.type = requireNonNull(type, "type");
        this.properties = requireNonNull(properties, "configuration properties");
        this.formatter = properties.getNameFormatter();
        this.serializers = buildSerializerMap();
        this.postProcessor = createPostProcessorFromAnnotatedMethod();
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

            final Object serializedValue = serializeElement(element, elementValue);
            final String formattedName = formatter.format(element.name());
            result.put(formattedName, serializedValue);
        }

        return result;
    }

    protected final Object serializeElement(E element, Object value) {
        // This cast can lead to a ClassCastException if an element of type X is
        // serialized by a custom serializer that expects a different type Y.
        @SuppressWarnings("unchecked")
        final Serializer<Object, Object> serializer = (Serializer<Object, Object>) serializers.get(element.name());
        try {
            return (value != null) ? serializer.serialize(value) : null;
        } catch (ClassCastException e) {
            String msg = String.format(
                    "Serialization of value '%s' for element '%s' of type '%s' failed.\n" +
                    "The type of the object to be serialized does not match the type " +
                    "the custom serializer of type '%s' expects.",
                    value, element.element(), element.declaringType(), serializer.getClass()
            );
            throw new ConfigurationException(msg, e);
        }
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

    protected final Object[] deserializeConfigurationElements(
            Map<?, ?> serializedConfiguration
    ) {
        final List<E> elements = elements();
        final Object[] result = new Object[elements.size()];

        for (int i = 0, size = elements.size(); i < size; i++) {
            final E element = elements.get(i);
            final String formattedName = formatter.format(element.name());

            if (!serializedConfiguration.containsKey(formattedName)) {
                final Object defaultValue = getDefaultValueOf(element);
                result[i] = applyPostProcessorForElement(element, defaultValue);
                continue;
            }

            final Object serializedValue = serializedConfiguration.get(formattedName);

            if ((serializedValue == null) && properties.inputNulls()) {
                // This statement (and hence the whole block) could be removed,
                // but in my opinion the code is clearer this way.
                result[i] = null;
            } else if (serializedValue == null) {
                result[i] = getDefaultValueOf(element);
            } else {
                result[i] = deserialize(element, serializedValue);
            }

            result[i] = applyPostProcessorForElement(element, result[i]);
        }

        return result;
    }

    private Object applyPostProcessorForElement(
            ConfigurationElement<?> element,
            Object deserializeValue
    ) {
        Object result = deserializeValue;

        boolean postProcessed = false;
        for (final Map.Entry<Predicate<? super ConfigurationElement<?>>, UnaryOperator<?>> entry : properties.getPostProcessorsByCondition().entrySet()) {
            final Predicate<? super ConfigurationElement<?>> condition = entry.getKey();

            if (!condition.test(element)) continue;

            final UnaryOperator<?> postProcessor = entry.getValue();
            result = tryApplyPostProcessorForElement(element, postProcessor, result);
            postProcessed = true;
        }

        if ((result == null) && postProcessed)
            requirePostProcessorDoesNotReturnNullForPrimitiveElement(element);
        else if (result == null)
            requireNonPrimitiveType(element);

        return result;
    }

    private static Object tryApplyPostProcessorForElement(
            ConfigurationElement<?> element,
            UnaryOperator<?> postProcessor,
            Object value
    ) {
        try {
            // This cast can lead to a ClassCastException if an element of type X is
            // annotated with a post-processor that takes values of some other type Y.
            @SuppressWarnings("unchecked")
            final UnaryOperator<Object> pp = (UnaryOperator<Object>) postProcessor;
            return pp.apply(value);
        } catch (ClassCastException e) {
            String msg = String.format(
                    "Deserialization of value '%s' for element '%s' of type '%s' failed.\n" +
                    "The type of the object to be deserialized does not match the type " +
                    "post-processor '%s' expects.",
                    value, element.element(), element.declaringType(), postProcessor
            );
            throw new ConfigurationException(msg, e);
        }
    }

    private static void requirePostProcessorDoesNotReturnNullForPrimitiveElement(
            ConfigurationElement<?> element
    ) {
        if (!element.type().isPrimitive()) return;

        if (element instanceof FieldElement) {
            final FieldElement fieldElement = (FieldElement) element;
            final Field field = fieldElement.element();
            String msg = String.format(
                    "Post-processors must not return null for primitive fields " +
                    "but some post-processor of field '%s' does.",
                    field
            );
            throw new ConfigurationException(msg);
        }

        throw new ConfigurationException("Unhandled ConfigurationElement: " + element);
    }

    private static void requireNonPrimitiveType(ConfigurationElement<?> element) {
        if (!element.type().isPrimitive()) return;

        if (element instanceof FieldElement) {
            final FieldElement fieldElement = (FieldElement) element;
            final Field field = fieldElement.element();
            String msg = String.format(
                    "Cannot set field '%s' to null value. " +
                    "Primitive types cannot be assigned null.",
                    field
            );
            throw new ConfigurationException(msg);
        }

        throw new ConfigurationException("Unhandled ConfigurationElement: " + element);
    }

    final UnaryOperator<T> createPostProcessorFromAnnotatedMethod() {
        final List<Method> list = Arrays.stream(type.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(PostProcess.class))
                .filter(x -> !x.isSynthetic())
                .filter(x -> !this.isAccessorMethod(x))
                .collect(Collectors.toList());

        if (list.isEmpty())
            return UnaryOperator.identity();
        if (list.size() > 1) {
            String methodNames = list.stream().map(Method::toString).collect(Collectors.joining("\n  "));
            String msg = String.format(
                    "Configuration types must not define more than one method for " +
                    "post-processing but type '%s' defines %d:\n  %s",
                    type, list.size(), methodNames
            );
            throw new ConfigurationException(msg);
        }

        final Method method = list.get(0);
        final int modifiers = method.getModifiers();
        if (Modifier.isAbstract(modifiers) || Modifier.isStatic(modifiers)) {
            String msg = String.format(
                    "Post-processing methods must be neither abstract nor static, " +
                    "but post-processing method '%s' of type '%s' is.",
                    method, type
            );
            throw new ConfigurationException(msg);
        }

        final int parameterCount = method.getParameterCount();
        if (parameterCount > 0) {
            String msg = String.format(
                    "Post-processing methods must not define any parameters but " +
                    "post-processing method '%s' of type '%s' defines %d.",
                    method, type, parameterCount
            );
            throw new ConfigurationException(msg);
        }

        final Class<?> returnType = method.getReturnType();
        if ((returnType != void.class) && (returnType != type)) {
            String msg = String.format(
                    "The return type of post-processing methods must either be 'void' or " +
                    "the same type as the configuration type in which the post-processing " +
                    "method is defined. The return type of the post-processing method of " +
                    "type '%s' is neither 'void' nor '%s'.",
                    type, type.getSimpleName()
            );
            throw new ConfigurationException(msg);
        }

        return object -> {
            if (method.getReturnType() == void.class) {
                Reflect.invoke(method, object);
                return object;
            }
            // The following cast won't fail because our last two checks from above
            // guarantee that the return type of the method equals T at this point.
            @SuppressWarnings("unchecked")
            T result = (T) Reflect.invoke(method, object);
            return result;
        };
    }

    // NOTE: this was originally a check intended for records
    final boolean isAccessorMethod(Method method) {
        return false;
    }

    protected abstract void requireSerializableElements();

    protected abstract String baseDeserializeExceptionMessage(E element, Object value);

    protected abstract List<E> elements();

    /**
     * Returns the default value of a field or record component before any
     * post-processing has been performed.
     *
     * @param element the configuration element
     * @return the default value for that element
     */
    protected abstract Object getDefaultValueOf(E element);

    abstract T newDefaultInstance();
}
