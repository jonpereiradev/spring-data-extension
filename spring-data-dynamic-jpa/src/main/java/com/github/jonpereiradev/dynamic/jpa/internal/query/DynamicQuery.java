package com.github.jonpereiradev.dynamic.jpa.internal.query;

import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKey;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;

import java.util.Optional;

public interface DynamicQuery {

    QueryInspectorResult getSelectQuery();

    QueryInspectorResult getCountQuery();

    void addJoin(QueryExpression expression);

    void addFilter(QueryExpression expression);

    Optional<QueryExpression> getJoinValue(QueryExpressionKey expressionKey);

    Optional<QueryExpression> getFilterValue(QueryExpressionKey expressionKey);

    Class<?> getEntityClass();

}
