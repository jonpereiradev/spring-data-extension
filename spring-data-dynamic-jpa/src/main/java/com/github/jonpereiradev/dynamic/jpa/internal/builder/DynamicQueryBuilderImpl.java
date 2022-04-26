package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotation;
import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotationReader;
import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotationReaderFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Id;

final class DynamicQueryBuilderImpl implements DynamicQueryBuilder {

    private final StringBuilder internal;

    private final Class<?> entityClass;
    private final QueryInspector inspector;

    DynamicQueryBuilderImpl(Class<?> entityClass) {
        this.internal = new StringBuilder();
        this.entityClass = entityClass;
        this.inspector = QueryInspectorFactory.newInspector();
    }

    @Override
    public SelectQueryBuilder select() {
        String aliasName = entityClass.getSimpleName().toLowerCase();
        String query = String.format("select %s from %s %s", aliasName, entityClass.getSimpleName(), aliasName);
        QueryInspectorResult result = inspector.inspect(query);

        return select(result);
    }

    @Override
    public SelectQueryBuilder select(Query query) {
        if (query.value().isEmpty()) {
            return select();
        }

        QueryInspectorResult result = inspector.inspect(query.value());

        return select(result);
    }

    @Override
    public SelectQueryBuilder select(QueryInspectorResult result) {
        internal.append("select ");

        if (result.isDistinct()) {
            internal.append("distinct ");
        }

        for (int i = 0; i < result.getSelect().length; i++) {
            if (i == 0) {
                internal.append(result.getSelect()[i]);
                continue;
            }

            internal.append(", ").append(result.getSelect()[i]);
        }

        return new SelectQueryBuilderImpl(new StringBuilder(internal), result);
    }

    @Override
    public SelectQueryBuilder count() {
        JpaAnnotationReaderFactory factory = new JpaAnnotationReaderFactory();
        JpaAnnotationReader reader = factory.createReader(entityClass);
        JpaAnnotation<Id> annotation = reader.findFirstNameOf(Id.class);

        String aliasName = entityClass.getSimpleName().toLowerCase();
        String query = String.format("select count(%s.%s) from %s %s", aliasName, annotation.getName(), entityClass.getSimpleName(), aliasName);
        QueryInspectorResult result = inspector.inspect(query);

        return count(result);
    }

    @Override
    public SelectQueryBuilder count(Query query) {
        QueryInspectorResult result;

        if (!query.countQuery().isEmpty()) {
            result = inspector.inspect(query.countQuery());
        } else if (!query.value().isEmpty()) {
            result = inspector.inspect(query.value());
        } else {
            return count();
        }

        return count(result);
    }

    @Override
    public SelectQueryBuilder count(QueryInspectorResult result) {
        JpaAnnotationReaderFactory factory = new JpaAnnotationReaderFactory();
        JpaAnnotationReader reader = factory.createReader(entityClass);
        JpaAnnotation<Id> annotation = reader.findFirstNameOf(Id.class);

        internal.append("select count(");

        if (result.isDistinct()) {
            internal.append("distinct ");
        }

        internal
            .append(result.getFrom()[0].getAliasName())
            .append(".")
            .append(annotation.getName())
            .append(")");

        return new SelectQueryBuilderImpl(new StringBuilder(internal), result);
    }

}
