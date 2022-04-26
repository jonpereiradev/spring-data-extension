package com.github.jonpereiradev.dynamic.jpa.internal.annotation;

import com.github.jonpereiradev.dynamic.jpa.Entities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.persistence.Id;

import java.lang.annotation.Annotation;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MethodAnnotationReaderTest {

    private JpaAnnotationReader reader;

    @Test
    void must_get_valid_jpa_annotation_type() {
        reader = new MethodAnnotationReader(Entities.MethodEntity.class);
        assertEquals(JpaAnnotationType.METHOD, reader.getType());
    }

    @Test
    void must_get_name_from_method_entity_class() {
        reader = new MethodAnnotationReader(Entities.MethodEntity.class);
        JpaAnnotation<Id> fieldName = reader.findFirstNameOf(Id.class);
        assertEquals("id", fieldName.getName());
    }

    @Test
    void must_get_name_from_extend_method_class() {
        reader = new MethodAnnotationReader(Entities.BaseMethodEntity.class);
        JpaAnnotation<Id> fieldName = reader.findFirstNameOf(Id.class);
        assertEquals("id", fieldName.getName());
    }

    @Test
    void must_get_empty_annotations_from_method_transient_class() {
        reader = new MethodAnnotationReader(Entities.TransientMethodEntity.class);
        List<JpaAnnotation<Annotation>> annotations = reader.findJpaAnnotations();
        assertTrue(annotations.isEmpty());
    }

    @Test
    void must_get_jpa_annotations_from_method_class() {
        reader = new MethodAnnotationReader(Entities.MethodEntity.class);
        List<JpaAnnotation<Annotation>> annotations = reader.findJpaAnnotations();

        Assertions.assertEquals(1, annotations.size());
        Assertions.assertEquals("id", annotations.get(0).getName());
        Assertions.assertEquals(Id.class, annotations.get(0).getAnnotation().annotationType());
        Assertions.assertEquals(int.class, annotations.get(0).getReturnType());
    }

}