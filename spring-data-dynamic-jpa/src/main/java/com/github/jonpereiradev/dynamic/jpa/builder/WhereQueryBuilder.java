package com.github.jonpereiradev.dynamic.jpa.builder;


import com.github.jonpereiradev.dynamic.jpa.QueryExpression;

public interface WhereQueryBuilder {

    WhereQueryBuilder and(QueryExpression expression);

    OrderByQueryBuilder order();

    String getQuery();

}
