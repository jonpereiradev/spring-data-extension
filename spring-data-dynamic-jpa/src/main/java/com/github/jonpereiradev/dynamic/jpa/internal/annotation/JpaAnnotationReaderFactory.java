package com.github.jonpereiradev.dynamic.jpa.internal.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JpaAnnotationReaderFactory {

    public JpaAnnotationReader createReader(Class<?> entityClass) {
        JpaAnnotationReaderImpl reader = new JpaAnnotationReaderImpl();

        for (Field declaredField : entityClass.getDeclaredFields()) {
            for (Annotation annotation : declaredField.getAnnotations()) {
                if (reader.isJpaAnnotation(annotation)) {
                    reader.reader = new FieldAnnotationReader(entityClass);
                    return reader;
                }
            }
        }

        for (Method declaredMethod : entityClass.getDeclaredMethods()) {
            for (Annotation annotation : declaredMethod.getAnnotations()) {
                if (reader.isJpaAnnotation(annotation)) {
                    reader.reader = new MethodAnnotationReader(entityClass);
                    return reader;
                }
            }
        }

        return reader;
    }

}
