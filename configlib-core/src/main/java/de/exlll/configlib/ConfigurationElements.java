package de.exlll.configlib;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;

final class ConfigurationElements {
    private ConfigurationElements() {}

    static class FieldElement implements ConfigurationElement<Field> {
        private final Field element;

        public FieldElement(Field element) {
            this.element = element;
        }

        @Override
        public Field element() {
            return this.element;
        }

        @Override
        public String name() {
            return element.getName();
        }

        @Override
        public Class<?> type() {
            return element.getType();
        }

        @Override
        public AnnotatedType annotatedType() {
            return element.getAnnotatedType();
        }

        @Override
        public Object value(Object elementHolder) {
            return Reflect.getValue(element, elementHolder);
        }

        @Override
        public Class<?> declaringType() {
            return element.getDeclaringClass();
        }
    }

//    record RecordComponentElement(RecordComponent element)
//            implements ConfigurationElement<RecordComponent> {
//        @Override
//        public String name() {
//            return element.getName();
//        }
//
//        @Override
//        public Class<?> type() {
//            return element.getType();
//        }
//
//        @Override
//        public AnnotatedType annotatedType() {
//            return element.getAnnotatedType();
//        }
//
//        @Override
//        public Object value(Object elementHolder) {
//            return Reflect.getValue(element, elementHolder);
//        }
//
//        @Override
//        public Class<?> declaringType() {
//            return element.getDeclaringRecord();
//        }
//    }
}
