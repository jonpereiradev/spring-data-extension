package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import javax.persistence.MappedSuperclass;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassJpaAnnotationReader {

    private final Class<?> entityClass;

    public ClassJpaAnnotationReader(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String findNameByAnnotation(Class<? extends Annotation> annotation) {
        String fieldNameByAnnotation = findFieldNameByAnnotation(entityClass, annotation);

        if (fieldNameByAnnotation != null) {
            return fieldNameByAnnotation;
        }

        return findMethodNameByAnnotation(entityClass, annotation);
    }

    private String findFieldNameByAnnotation(Class<?> entityClass, Class<? extends Annotation> annotation) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation)) {
                return field.getName();
            }
        }

        if (entityClass.getSuperclass().isAnnotationPresent(MappedSuperclass.class)) {
            return findFieldNameByAnnotation(entityClass.getSuperclass(), annotation);
        }

        return null;
    }

    private String findMethodNameByAnnotation(Class<?> entityClass, Class<? extends Annotation> annotation) {
        for (Method method : entityClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                String methodName = method.getName();
                int fieldNameStart = -1;

                for (int i = 0; i < methodName.length(); i++) {
                    if (Character.isUpperCase(methodName.charAt(i))) {
                        fieldNameStart = i;
                        break;
                    }
                }

                return String.valueOf(methodName.charAt(fieldNameStart)).toLowerCase() + methodName.substring(fieldNameStart + 1);
            }
        }

        if (entityClass.getSuperclass().isAnnotationPresent(MappedSuperclass.class)) {
            return findMethodNameByAnnotation(entityClass.getSuperclass(), annotation);
        }

        return null;
    }

}
