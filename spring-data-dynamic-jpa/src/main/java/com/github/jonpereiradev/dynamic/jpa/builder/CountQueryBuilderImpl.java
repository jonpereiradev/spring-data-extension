package com.github.jonpereiradev.dynamic.jpa.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.QueryInspect;
import com.github.jonpereiradev.dynamic.jpa.internal.QueryProcessorImpl;
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

        QueryInspect inspect = QueryProcessorImpl.process(internal.toString());
        return new FromQueryBuilderImpl(new StringBuilder(internal), inspect);
    }

    @Override
    public FromQueryBuilder from(Class<?> entityClass, Query query) {
        QueryInspect inspect;

        if (!query.countQuery().isEmpty()) {
            inspect = QueryProcessorImpl.process(query.countQuery());
        } else if (!query.value().isEmpty()) {
            inspect = QueryProcessorImpl.process(query.value());
        } else {
            return from(entityClass);
        }

        return from(entityClass, inspect);
    }

    @Override
    public FromQueryBuilder from(Class<?> entityClass, QueryInspect inspect) {
        ClassJpaAnnotationReader reader = new ClassJpaAnnotationReader(entityClass);
        String idFieldName = reader.findNameByAnnotation(Id.class);

        internal.append("select count(");

        if (inspect.isDistinct()) {
            internal.append("distinct ");
        }

        internal
            .append(inspect.getAliasName())
            .append(".")
            .append(idFieldName)
            .append(") from ")
            .append(inspect.getEntityName())
            .append(" ")
            .append(inspect.getAliasName());

        return new FromQueryBuilderImpl(new StringBuilder(internal), inspect);
    }
}
