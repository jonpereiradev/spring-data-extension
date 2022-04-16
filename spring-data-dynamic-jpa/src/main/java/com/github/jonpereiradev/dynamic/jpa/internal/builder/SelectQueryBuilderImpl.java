package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import org.springframework.data.jpa.repository.Query;

final class SelectQueryBuilderImpl implements SelectQueryBuilder {

    private final StringBuilder internal;
    private final String alias;

    SelectQueryBuilderImpl(StringBuilder internal, String alias) {
        this.internal = internal;
        this.alias = alias;
    }

    @Override
    public FromQueryBuilder from(Class<?> entityClass) {
        internal
            .append("select ")
            .append(alias)
            .append(" from ")
            .append(entityClass.getSimpleName())
            .append(" ")
            .append(alias);

        QueryInspector inspector = QueryInspectorFactory.newInspector();
        QueryInspectorResult inspectorResult = inspector.inspect(internal.toString());
        return new FromQueryBuilderImpl(new StringBuilder(internal), inspectorResult);
    }

    @Override
    public FromQueryBuilder from(Class<?> entityClass, Query query) {
        if (query.value().isEmpty()) {
            return from(entityClass);
        }

        QueryInspector inspect = QueryInspectorFactory.newInspector();
        QueryInspectorResult inspectorResult = inspect.inspect(query.value());
        return from(entityClass, inspectorResult);
    }

    @Override
    public FromQueryBuilder from(Class<?> entityClass, QueryInspectorResult result) {
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

        internal.append(" from ");

        for (int i = 0; i < result.getFrom().length; i++) {
            QueryInspectorResult.DynamicFrom from = result.getFrom()[i];

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

        return new FromQueryBuilderImpl(new StringBuilder(internal), result);
    }

}