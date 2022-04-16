package com.github.jonpereiradev.dynamic.jpa.internal.builder;


import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import org.springframework.data.jpa.repository.Query;

public interface SelectQueryBuilder {

    FromQueryBuilder from(Class<?> entityClass);

    FromQueryBuilder from(Class<?> entityClass, Query query);

    FromQueryBuilder from(Class<?> entityClass, QueryInspectorResult inspectorResult);

}
