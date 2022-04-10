package com.github.jonpereiradev.dynamic.jpa.builder;


import com.github.jonpereiradev.dynamic.jpa.internal.QueryInspect;
import org.springframework.data.jpa.repository.Query;

public interface SelectQueryBuilder {

    FromQueryBuilder from(Class<?> entityClass);

    FromQueryBuilder from(Class<?> entityClass, Query query);

    FromQueryBuilder from(Class<?> entityClass, QueryInspect inspect);

}
