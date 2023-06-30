package de.exlll.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated type is polymorphic. Serializers for polymorphic types are not
 * selected based on the compile-time types of configuration elements, but instead are chosen at
 * runtime based on the actual types of their values. This enables adding instances of subclasses /
 * implementations of a polymorphic type to collections. The subtypes must be valid configurations.
 * <p>
 * For correct deserialization, if an instance of polymorphic type (or one of its implementations /
 * subclasses) is serialized, an additional property that holds type information is added to its
 * serialization. The type information can be customized using the {@link PolymorphicTypes}
 * annotation.
 *
 * @see PolymorphicTypes
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SerializeWith(serializer = PolymorphicSerializer.class)
public @interface Polymorphic {
    /**
     * The default name of the property that holds the type information.
     */
    String DEFAULT_PROPERTY = "type";

    /**
     * Returns the name of the property that holds the type information.
     * <p>
     * The property returned by this method must neither be blank nor be the
     * name of a configuration element.
     *
     * @return name of the property that holds the type information
     * @see String#isEmpty()
     */
    String property() default DEFAULT_PROPERTY;
}
