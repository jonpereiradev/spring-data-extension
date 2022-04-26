package com.github.jonpereiradev.dynamic.jpa.internal.expression;

public interface QueryExpressionKey {

    String GLOBAL_PREFIX = "clazz.";

    boolean isGlobalKey();

    String getKey();

    String getGlobalKey();

}
