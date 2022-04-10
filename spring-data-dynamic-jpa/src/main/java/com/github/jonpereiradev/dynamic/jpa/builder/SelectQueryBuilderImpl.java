package com.github.jonpereiradev.dynamic.jpa.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.QueryInspect;
import com.github.jonpereiradev.dynamic.jpa.internal.QueryProcessorImpl;
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

        QueryInspect inspect = QueryProcessorImpl.process(internal.toString());
        return new FromQueryBuilderImpl(new StringBuilder(internal), inspect);
    }

    @Override
    public FromQueryBuilder from(Class<?> entityClass, Query query) {
        if (query.value().isEmpty()) {
            return from(entityClass);
        }

        QueryInspect inspect = QueryProcessorImpl.process(query.value());
        return from(entityClass, inspect);
    }

    @Override
    public FromQueryBuilder from(Class<?> entityClass, QueryInspect inspect) {
        internal.append("select ");

        if (inspect.isDistinct()) {
            internal.append("distinct ");
        }

        for (int i = 0; i < inspect.getFields().length; i++) {
            if (i == 0) {
                internal.append(inspect.getFields()[i]);
                continue;
            }

            internal.append(", ").append(inspect.getFields()[i]);
        }

        internal
            .append(" from ")
            .append(inspect.getEntityName())
            .append(" ")
            .append(inspect.getAliasName());

        return new FromQueryBuilderImpl(new StringBuilder(internal), inspect);
    }

}