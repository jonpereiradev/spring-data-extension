package com.github.jonpereiradev.dynamic.jpa.internal.annotation;

import com.github.jonpereiradev.dynamic.jpa.repository.AutoScanFilterDisabled;

import javax.persistence.MappedSuperclass;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

class MethodAnnotationReader implements JpaAnnotationReader {

    private final Class<?> entityClass;

    MethodAnnotationReader(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public <T extends Annotation> JpaAnnotation<T> findFirstNameOf(Class<T> annotation) {
        return findFirstNameOf(entityClass, annotation);
    }

    private <T extends Annotation> JpaAnnotation<T> findFirstNameOf(Class<?> entityClass, Class<T> annotationClass) {
        for (Method method : entityClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotationClass)) {
                return new JpaAnnotation<>(method, method.getAnnotation(annotationClass));
            }
        }

        Class<?> superclass = entityClass.getSuperclass();

        if (superclass != null && superclass.isAnnotationPresent(MappedSuperclass.class)) {
            return findFirstNameOf(superclass, annotationClass);
        }

        return null;
    }

    @Override
    public <T extends Annotation> List<JpaAnnotation<T>> findJpaAnnotations() {
        return getJpaAnnotations(entityClass, new ArrayList<>());
    }

    @SuppressWarnings("unchecked")
    private <T extends Annotation> List<JpaAnnotation<T>> getJpaAnnotations(Class<?> entityClass, List<JpaAnnotation<T>> jpaAnnotations) {
        for (Method method : entityClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(AutoScanFilterDisabled.class)) {
                continue;
            }

            for (Annotation annotation : method.getAnnotations()) {
                if (isJpaAnnotation(annotation)) {
                    jpaAnnotations.add(new JpaAnnotation<>(method, (T) annotation));
                }
            }
        }

        Class<?> superclass = entityClass.getSuperclass();

        if (superclass != null && superclass.isAnnotationPresent(MappedSuperclass.class)) {
            return getJpaAnnotations(superclass, jpaAnnotations);
        }

        return jpaAnnotations;
    }

    @Override
    public JpaAnnotationType getType() {
        return JpaAnnotationType.METHOD;
    }

}
