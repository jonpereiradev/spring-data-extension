package com.github.jonpereiradev.dynamic.jpa.builder;


import com.github.jonpereiradev.dynamic.jpa.QueryExpression;

public interface FromQueryBuilder {

    FromQueryBuilder join();

    FromQueryBuilder join(QueryExpression queryExpression);

    WhereQueryBuilder where();

    OrderByQueryBuilder orderBy();

    String getQuery();

}
