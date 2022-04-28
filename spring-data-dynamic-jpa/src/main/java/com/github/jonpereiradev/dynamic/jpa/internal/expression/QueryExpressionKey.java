package com.github.jonpereiradev.dynamic.jpa.internal.expression;

public interface QueryExpressionKey {

    boolean isGlobalKey();

    String getKey();

    String getGlobalKey();

}
