package com.github.jonpereiradev.dynamic.jpa.internal.annotation;

import com.github.jonpereiradev.dynamic.jpa.repository.AutoScanDisabled;

import javax.persistence.MappedSuperclass;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

class FieldAnnotationReader implements JpaAnnotationReader {

    private final Class<?> entityClass;

    FieldAnnotationReader(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public <T extends Annotation> JpaAnnotation<T> findFirstNameOf(Class<T> annotation) {
        return findFirstNameOf(entityClass, annotation);
    }

    private <T extends Annotation> JpaAnnotation<T> findFirstNameOf(Class<?> entityClass, Class<T> annotationClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass)) {
                return new JpaAnnotation<>(field, field.getAnnotation(annotationClass));
            }
        }

        if (entityClass.getSuperclass().isAnnotationPresent(MappedSuperclass.class)) {
            return findFirstNameOf(entityClass.getSuperclass(), annotationClass);
        }

        return null;
    }

    @Override
    public <T extends Annotation> List<JpaAnnotation<T>> findJpaAnnotations() {
        return findJpaAnnotations(entityClass, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> List<JpaAnnotation<T>> findJpaAnnotations(Class<?> entityClass, List<JpaAnnotation<T>> jpaAnnotations) {
        if (entityClass.isAnnotationPresent(AutoScanDisabled.class)) {
            return jpaAnnotations;
        }

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(AutoScanDisabled.class)) {
                continue;
            }

            for (Annotation annotation : field.getAnnotations()) {
                if (isJpaAnnotation(annotation)) {
                    jpaAnnotations.add(new JpaAnnotation<>(field, (T) annotation));
                }
            }
        }

        Class<?> superclass = entityClass.getSuperclass();

        if (superclass != null && superclass.isAnnotationPresent(MappedSuperclass.class)) {
            return findJpaAnnotations(superclass, jpaAnnotations);
        }

        return jpaAnnotations;
    }

    @Override
    public JpaAnnotationType getType() {
        return JpaAnnotationType.FIELD;
    }

}