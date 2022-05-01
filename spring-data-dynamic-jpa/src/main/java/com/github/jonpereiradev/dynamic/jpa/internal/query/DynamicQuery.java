package com.github.jonpereiradev.dynamic.jpa.internal.query;

import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKey;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;

import java.util.Optional;

public interface DynamicQuery {

    QueryInspectorResult getSelectQuery();

    QueryInspectorResult getCountQuery();

    void addExpression(QueryExpression expression);

    Optional<QueryExpression> getExpression(QueryExpressionKey expressionKey);

    Class<?> getEntityClass();

}
