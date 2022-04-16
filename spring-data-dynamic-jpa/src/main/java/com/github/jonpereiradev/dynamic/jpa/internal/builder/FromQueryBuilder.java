package com.github.jonpereiradev.dynamic.jpa.internal.builder;


import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;

public interface FromQueryBuilder {

    FromQueryBuilder join();

    FromQueryBuilder join(QueryExpression queryExpression);

    WhereQueryBuilder where();

    OrderQueryBuilder order();

    String getQuery();

}
