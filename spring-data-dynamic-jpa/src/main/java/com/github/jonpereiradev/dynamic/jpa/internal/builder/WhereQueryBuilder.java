package com.github.jonpereiradev.dynamic.jpa.internal.builder;


import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;

public interface WhereQueryBuilder {

    WhereQueryBuilder and(QueryExpression expression);

    OrderQueryBuilder order();

}
