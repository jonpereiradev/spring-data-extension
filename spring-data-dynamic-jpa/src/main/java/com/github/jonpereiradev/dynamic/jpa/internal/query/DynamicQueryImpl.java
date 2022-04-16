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

    private final Map<String, QueryExpression> joins;
    private final Map<String, QueryExpression> filters;

    DynamicQueryImpl(String selectQuery, String countQuery, Class<?> entityClass, Class<?> repositoryInterface) {
        QueryInspector inspector = QueryInspectorFactory.newInspector();
        this.selectQuery = inspector.inspect(selectQuery);
        this.countQuery = inspector.inspect(countQuery);
        this.joins = new HashMap<>();
        this.filters = new HashMap<>();
    }

    @Override
    public void addJoin(QueryExpression expression) {
        joins.put(expression.getKey(), expression);
    }

    @Override
    public void addFilter(QueryExpression expression) {
        filters.put(expression.getKey(), expression);
    }

    @Override
    public Optional<QueryExpression> getJoinValue(QueryExpressionKey expressionKey) {
        String keyName = expressionKey.getKey();

        if (!joins.containsKey(keyName)) {
            keyName = expressionKey.getGlobalKey();
        }

        return Optional.ofNullable(joins.get(keyName));
    }

    @Override
    public Optional<QueryExpression> getFilterValue(QueryExpressionKey expressionKey) {
        String keyName = expressionKey.getKey();

        if (!filters.containsKey(keyName)) {
            keyName = expressionKey.getGlobalKey();
        }

        return Optional.ofNullable(filters.get(keyName));
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
        return null;
    }

}
