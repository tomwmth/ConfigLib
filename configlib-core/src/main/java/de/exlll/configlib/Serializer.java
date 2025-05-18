package de.exlll.configlib;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementations of this interface convert instances of type {@link T1} to a serializable type
 * {@link T2} and vice versa.
 * <p>
 * Which types {@link T2} are serializable depends on the underlying storage system. Currently,
 * all storage systems support the following target types:
 * <ul>
 *     <li>{@link Boolean}</li>
 *     <li>{@link Long}</li>
 *     <li>{@link Double}</li>
 *     <li>{@link String}</li>
 *     <li>(Nested) {@link List}s of the other types</li>
 *     <li>(Nested) {@link Set}s of the other types</li>
 *     <li>(Nested) {@link Map}s of the other types</li>
 * </ul>
 * <p>
 * For all custom serializers, {@link T2} must be one of the six types listed above.
 *
 * @param <T1> the type of the objects that should be serialized
 * @param <T2> the serializable type
 */
public interface Serializer<T1, T2> {
    /**
     * Serializes an element of type {@link T1} into an element of type {@link T2}.
     * Type {@link T2} must be a valid target type.
     *
     * @param element the element of type {@link T1} that is serialized
     * @return the serialized element of type {@link T2}
     */
    T2 serialize(T1 element);

    /**
     * Deserializes an element of type {@link T2} into an element of type {@link T1}.
     *
     * @param element the element of type {@link T2} that is deserialized
     * @return the deserialized element of type {@link T1}
     */
    T1 deserialize(T2 element);
}
