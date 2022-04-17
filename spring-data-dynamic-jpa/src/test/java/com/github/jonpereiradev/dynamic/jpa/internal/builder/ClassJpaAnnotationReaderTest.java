package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.TestEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;


class ClassJpaAnnotationReaderTest {

    private ClassJpaAnnotationReader reader;

    @Test
    void must_get_id_field_name_from_field_simple_class() {
        reader = new ClassJpaAnnotationReader(TestEntity.class);
        String fieldName = reader.findNameByAnnotation(Id.class);
        Assertions.assertEquals("id", fieldName);
    }

    @Test
    void must_get_id_field_name_from_field_extend_class() {
        reader = new ClassJpaAnnotationReader(ExampleExtendWithField.class);
        String fieldName = reader.findNameByAnnotation(Id.class);
        Assertions.assertEquals("id", fieldName);
    }

    @Test
    void must_get_id_field_name_from_method_simple_class() {
        reader = new ClassJpaAnnotationReader(ExampleWithMethod.class);
        String fieldName = reader.findNameByAnnotation(Id.class);
        Assertions.assertEquals("id", fieldName);
    }

    @Test
    void must_get_id_field_name_from_method_extend_class() {
        reader = new ClassJpaAnnotationReader(ExampleExtendWithMethod.class);
        String fieldName = reader.findNameByAnnotation(Id.class);
        Assertions.assertEquals("id", fieldName);
    }

    @MappedSuperclass
    private static class BaseExampleWithField {

        @Id
        private String id;

    }

    private static class ExampleExtendWithField extends BaseExampleWithField {
    }

    private static class ExampleWithMethod {

        @Id
        public String getId() {
            return null;
        }

    }

    @MappedSuperclass
    private static class BaseExampleWithMethod {

        @Id
        public String getId() {
            return null;
        }

    }

    private static class ExampleExtendWithMethod extends BaseExampleWithMethod {

    }

}