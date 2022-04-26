package com.github.jonpereiradev.dynamic.jpa.internal.annotation;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

class NoneAnnotationReader implements JpaAnnotationReader {

    @Override
    public <T extends Annotation> JpaAnnotation<T> findFirstNameOf(Class<T> annotation) {
        return null;
    }

    @Override
    public <T extends Annotation> List<JpaAnnotation<T>> findJpaAnnotations() {
        return Collections.emptyList();
    }

    @Override
    public JpaAnnotationType getType() {
        return JpaAnnotationType.NONE;
    }

}