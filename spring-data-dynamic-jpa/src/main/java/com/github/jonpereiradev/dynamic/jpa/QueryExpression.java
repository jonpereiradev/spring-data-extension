package com.github.jonpereiradev.dynamic.jpa;


import java.util.function.Function;


interface QueryExpression {

    static QueryExpression newGlobalExpression(String name, String expression) {
        return newGlobalExpression(name, expression, DynamicQueryMatchers::none);
    }

    static QueryExpression newGlobalExpression(String name, String expression, Function<Object, ?> matcher) {
        return new QueryExpressionImpl("clazz." + name, expression, matcher);
    }

    static QueryExpression newExpression(String prefix, String name, String expression) {
        return newExpression(prefix, name, expression, DynamicQueryMatchers::none);
    }

    static QueryExpression newExpression(String prefix, String name, String expression, Function<Object, ?> matcher) {
        return new QueryExpressionImpl(prefix + "." + name, expression, matcher);
    }

    String getKey();

    String getBinding();

    String getExpression();

    Function<Object, ?> getMatcher();

}
