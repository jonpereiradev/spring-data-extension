package com.github.jonpereiradev.dynamic.jpa.internal.builder;


import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;

public interface OrderQueryBuilder {

    String getQuery();

    OrderQueryBuilder by(QueryExpression expression);

}
