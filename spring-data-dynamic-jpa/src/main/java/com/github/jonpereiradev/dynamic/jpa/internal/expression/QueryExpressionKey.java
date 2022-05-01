package com.github.jonpereiradev.dynamic.jpa.internal.expression;

public interface QueryExpressionKey {

    boolean isGlobal();

    String getValue();

    String getGlobalValue();

}
