package com.github.jonpereiradev.dynamic.jpa.internal.expression;


import java.util.function.Function;


public interface QueryExpression {

    QueryExpressionKey getKey();

    String getBinding();

    String getClause();

    boolean isFeature();

    Function<Object, ?> getMatcher();

}
