package com.github.jonpereiradev.dynamic.jpa.internal.expression;


import com.github.jonpereiradev.dynamic.jpa.repository.DynamicQueryMatchers;

import java.util.function.Function;

import static com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKey.GLOBAL_PREFIX;


public interface QueryExpression {

    static QueryExpression newGlobalExpression(String name, String expression) {
        return newGlobalExpression(name, expression, DynamicQueryMatchers::none);
    }

    static QueryExpression newGlobalExpression(String name, String expression, Function<Object, ?> matcher) {
        return new QueryExpressionImpl(GLOBAL_PREFIX + name, expression, matcher);
    }

    static QueryExpression newGlobalFeature(String name, String expression) {
        return new QueryExpressionImpl(GLOBAL_PREFIX + name, expression, true);
    }

    static QueryExpression newExpression(String prefix, String name, String expression) {
        return newExpression(prefix, name, expression, DynamicQueryMatchers::none);
    }

    static QueryExpression newExpression(String prefix, String name, String expression, Function<Object, ?> matcher) {
        return new QueryExpressionImpl(prefix + "." + name, expression, matcher);
    }

    static QueryExpression newFeature(String prefix, String name, String expression) {
        return new QueryExpressionImpl(prefix + "." + name, expression, true);
    }

    String getKey();

    String getBinding();

    String getClause();

    boolean isFeature();

    Function<Object, ?> getMatcher();

}
