package com.github.jonpereiradev.dynamic.jpa.internal.annotation;

import com.github.jonpereiradev.dynamic.jpa.Entities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JpaAnnotationReaderFactoryTest {

    private JpaAnnotationReaderFactory factory;

    @BeforeEach
    void before_each() {
        factory = new JpaAnnotationReaderFactory();
    }

    @Test
    void must_identify_annotation_none() {
        JpaAnnotationReader reader = factory.createReader(Entities.None.class);
        assertEquals(JpaAnnotationType.NONE, reader.getType());
    }

    @Test
    void must_identify_annotation_field() {
        JpaAnnotationReader reader = factory.createReader(Entities.FieldEntity.class);
        assertEquals(JpaAnnotationType.FIELD, reader.getType());
    }

    @Test
    void must_identify_annotation_method() {
        JpaAnnotationReader reader = factory.createReader(Entities.MethodEntity.class);
        assertEquals(JpaAnnotationType.METHOD, reader.getType());
    }

}