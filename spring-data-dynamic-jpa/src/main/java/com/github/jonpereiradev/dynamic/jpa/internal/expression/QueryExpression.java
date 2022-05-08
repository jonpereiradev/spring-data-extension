package com.github.jonpereiradev.dynamic.jpa.internal.expression;


import com.github.jonpereiradev.dynamic.jpa.converter.TypeConverter;


public interface QueryExpression {

    QueryExpressionKey getKey();

    String getBinding();

    String getClause();

    boolean isFeature();

    TypeConverter<?> getConverter();

}
