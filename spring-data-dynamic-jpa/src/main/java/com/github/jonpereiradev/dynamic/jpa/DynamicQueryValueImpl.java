package com.github.jonpereiradev.dynamic.jpa;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class DynamicQueryValueImpl implements DynamicQueryValue {

    private final String query;
    private final String countQuery;

    private final Map<String, QueryExpression> joins;
    private final Map<String, QueryExpression> filters;

    DynamicQueryValueImpl(String query, String countQuery) {
        this.query = query;
        this.countQuery = countQuery;
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
    public String getQuery() {
        return query;
    }

    @Override
    public String getCountQuery() {
        return countQuery;
    }

    @Override
    public Set<QueryExpression> getJoinExpressions() {
        return new HashSet<>(joins.values());
    }

    @Override
    public Set<QueryExpression> getFilterExpressions() {
        return new HashSet<>(filters.values());
    }

}
