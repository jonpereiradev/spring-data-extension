package com.github.jonpereiradev.dynamic.jpa.internal.query;


import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKey;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class DynamicQueryImpl implements DynamicQuery {

    private final QueryInspectorResult selectQuery;
    private final QueryInspectorResult countQuery;
    private final Class<?> entityClass;
    private final Map<QueryExpressionKey, QueryExpression> expressions;

    DynamicQueryImpl(String selectQuery, String countQuery, Class<?> entityClass) {
        QueryInspector inspector = QueryInspectorFactory.newInspector();
        this.selectQuery = inspector.inspect(selectQuery);
        this.countQuery = inspector.inspect(countQuery);
        this.entityClass = entityClass;
        this.expressions = new HashMap<>();
    }

    @Override
    public void addExpression(QueryExpression expression) {
        expressions.put(expression.getKey(), expression);
    }

    @Override
    public Optional<QueryExpression> getExpression(QueryExpressionKey expressionKey) {
        QueryExpression expression = expressions.get(expressionKey);
        return Optional.ofNullable(expression);
    }

    @Override
    public QueryInspectorResult getSelectQuery() {
        return selectQuery;
    }

    @Override
    public QueryInspectorResult getCountQuery() {
        return countQuery;
    }

    @Override
    public Class<?> getEntityClass() {
        return entityClass;
    }

}
