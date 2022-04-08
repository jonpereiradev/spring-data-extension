package com.github.jonpereiradev.dynamic.jpa;

import java.lang.reflect.Method;
import java.util.Set;

interface QueryExpressionFactory {

    Set<QueryExpression> createExpressions();

    Set<QueryExpression> createExpressions(Method method);

}
