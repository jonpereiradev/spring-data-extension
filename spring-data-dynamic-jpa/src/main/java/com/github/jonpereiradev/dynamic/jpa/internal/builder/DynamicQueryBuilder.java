package com.github.jonpereiradev.dynamic.jpa.internal.builder;


import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import org.springframework.data.jpa.repository.Query;

public interface DynamicQueryBuilder {

    SelectQueryBuilder select();

    SelectQueryBuilder select(Query query);

    SelectQueryBuilder select(QueryInspectorResult result);

    SelectQueryBuilder count();

    SelectQueryBuilder count(Query query);

    SelectQueryBuilder count(QueryInspectorResult result);

    static DynamicQueryBuilder newInstance(Class<?> entityClass) {
        return new DynamicQueryBuilderImpl(entityClass);
    }

}
