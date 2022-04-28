package com.github.jonpereiradev.dynamic.jpa.internal.expression;

import java.lang.reflect.Method;
import java.util.Set;

public interface QueryExpressionFactory {

    Set<QueryExpression> createExpressions();

    Set<QueryExpression> createExpressions(String aliasName);

    Set<QueryExpression> createExpressions(Method method);

}
