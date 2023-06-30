package de.exlll.configlib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the subtypes of {@code Polymorphic} types. This annotation can be used to provide type
 * aliases for subtypes which are then used instead of Java class names.
 *
 * @see Polymorphic
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PolymorphicTypes {
    /**
     * Returns (possibly only a subset of) the subtypes of the annotated type.
     *
     * @return subtypes of the annotated type
     */
    Type[] value();

    /**
     * Indicates a subtype of a {@code Polymorphic} type.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Type {
        /**
         * Returns the class of the subtype.
         *
         * @return class of the subtype
         */
        Class<?> type();

        /**
         * Returns the alias of the subtype. If the alias returned by this method is blank,
         * the Java class name ist used.
         *
         * @return alias of the subtype
         * @see String#isEmpty()
         */
        String alias() default "";
    }
}
