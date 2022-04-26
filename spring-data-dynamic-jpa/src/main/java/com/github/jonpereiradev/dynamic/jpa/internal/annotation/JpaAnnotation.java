package com.github.jonpereiradev.dynamic.jpa.internal.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class JpaAnnotation<T extends Annotation> {

    private final String name;
    private final T annotation;
    private final AnnotatedElement element;
    private final Class<?> returnType;

    public JpaAnnotation(Field field, T annotation) {
        this.name = field.getName();
        this.annotation = annotation;
        this.element = field;
        this.returnType = field.getType();
    }

    public JpaAnnotation(Method method, T annotation) {
        String methodName = method.getName();
        int fieldNameStart = -1;

        for (int i = 0; i < methodName.length(); i++) {
            if (Character.isUpperCase(methodName.charAt(i))) {
                fieldNameStart = i;
                break;
            }
        }
        String firstLetter = String.valueOf(methodName.charAt(fieldNameStart)).toLowerCase();
        String lastLetters = methodName.substring(fieldNameStart + 1);

        this.name = firstLetter + lastLetters;
        this.annotation = annotation;
        this.element = method;
        this.returnType = method.getReturnType();
    }

    public boolean isAnnotation(Class<? extends Annotation> annotationClass) {
        return element.isAnnotationPresent(annotationClass);
    }

    public String getName() {
        return name;
    }

    public T getAnnotation() {
        return annotation;
    }

    public Class<?> getReturnType() {
        return returnType;
    }
}
