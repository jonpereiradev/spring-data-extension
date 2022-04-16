package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.Id;

final class CountQueryBuilderImpl implements SelectQueryBuilder {

    private final StringBuilder internal;
    private final String alias;

    CountQueryBuilderImpl(StringBuilder internal, String alias) {
        this.internal = internal;
        this.alias = alias;
    }

    @Override
    public FromQueryBuilder from(Class<?> entityClass) {
        ClassJpaAnnotationReader reader = new ClassJpaAnnotationReader(entityClass);
        String idFieldName = reader.findNameByAnnotation(Id.class);

        internal
            .append("select count(")
            .append(alias)
            .append(".")
            .append(idFieldName)
            .append(") from ")
            .append(entityClass.getSimpleName())
            .append(" ")
            .append(alias);

        QueryInspector inspector = QueryInspectorFactory.newInspector();
        QueryInspectorResult inspectorResult = inspector.inspect(internal.toString());
        return new FromQueryBuilderImpl(new StringBuilder(internal), inspectorResult);
    }

    @Override
    public FromQueryBuilder from(Class<?> entityClass, Query query) {
        QueryInspectorResult inspect;
        QueryInspector inspector = QueryInspectorFactory.newInspector();

        if (!query.countQuery().isEmpty()) {
            inspect = inspector.inspect(query.countQuery());
        } else if (!query.value().isEmpty()) {
            inspect = inspector.inspect(query.value());
        } else {
            return from(entityClass);
        }

        return from(entityClass, inspect);
    }

    @Override
    public FromQueryBuilder from(Class<?> entityClass, QueryInspectorResult inspectorResult) {
        ClassJpaAnnotationReader reader = new ClassJpaAnnotationReader(entityClass);
        String idFieldName = reader.findNameByAnnotation(Id.class);

        internal.append("select count(");

        if (inspectorResult.isDistinct()) {
            internal.append("distinct ");
        }

        internal
            .append(inspectorResult.getFrom()[0].getAliasName())
            .append(".")
            .append(idFieldName)
            .append(") from ");

        for (int i = 0; i < inspectorResult.getFrom().length; i++) {
            QueryInspectorResult.DynamicFrom from = inspectorResult.getFrom()[i];

            if (i == 0) {
                internal
                    .append(from.getEntityName())
                    .append(" ")
                    .append(from.getAliasName());
            } else {
                internal
                    .append(", ")
                    .append(from.getEntityName())
                    .append(" ")
                    .append(from.getAliasName());
            }
        }

        return new FromQueryBuilderImpl(new StringBuilder(internal), inspectorResult);
    }
}
