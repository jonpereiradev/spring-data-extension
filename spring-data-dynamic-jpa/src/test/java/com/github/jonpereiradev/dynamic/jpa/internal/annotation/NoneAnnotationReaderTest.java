package com.github.jonpereiradev.dynamic.jpa.internal.annotation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.Id;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoneAnnotationReaderTest {

    private JpaAnnotationReader reader;

    @BeforeEach
    void before_each() {
        reader = new NoneAnnotationReader();
    }

    @Test
    void must_get_valid_jpa_annotation_type() {
        assertEquals(JpaAnnotationType.NONE, reader.getType());
    }

    @Test
    void must_get_empty_jpa_annotations() {
        assertTrue(reader.findJpaAnnotations().isEmpty());
    }

    @Test
    void must_get_null_field_name() {
        assertNull(reader.findFirstNameOf(Id.class));
    }

}