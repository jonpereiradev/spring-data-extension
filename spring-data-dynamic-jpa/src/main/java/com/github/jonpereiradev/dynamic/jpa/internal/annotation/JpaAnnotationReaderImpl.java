package com.github.jonpereiradev.dynamic.jpa.internal.annotation;

import java.lang.annotation.Annotation;
import java.util.List;

final class JpaAnnotationReaderImpl implements JpaAnnotationReader {

    JpaAnnotationReader reader;

    JpaAnnotationReaderImpl() {
        this.reader = new NoneAnnotationReader();
    }

    @Override
    public <T extends Annotation> JpaAnnotation<T> findFirstNameOf(Class<T> annotation) {
        return reader.findFirstNameOf(annotation);
    }

    @Override
    public <T extends Annotation> List<JpaAnnotation<T>> findJpaAnnotations() {
        return reader.findJpaAnnotations();
    }

    public JpaAnnotationType getType() {
        return reader.getType();
    }

}
