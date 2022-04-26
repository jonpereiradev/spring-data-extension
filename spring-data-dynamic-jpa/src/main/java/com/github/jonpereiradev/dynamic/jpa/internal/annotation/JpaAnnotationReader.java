package com.github.jonpereiradev.dynamic.jpa.internal.annotation;

import javax.persistence.Transient;
import java.lang.annotation.Annotation;
import java.util.List;

public interface JpaAnnotationReader {

    default boolean isJpaAnnotation(Annotation annotation) {
        return annotation.annotationType().getPackage().getName().startsWith("javax.persistence") && !annotation.annotationType().equals(Transient.class);
    }

    <T extends Annotation> JpaAnnotation<T> findFirstNameOf(Class<T> annotation);

    JpaAnnotationType getType();

    <T extends Annotation> List<JpaAnnotation<T>> findJpaAnnotations();

}
