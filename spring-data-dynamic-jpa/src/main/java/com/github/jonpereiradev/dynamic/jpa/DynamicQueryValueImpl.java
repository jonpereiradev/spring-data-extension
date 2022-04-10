package com.github.jonpereiradev.dynamic.jpa;


import com.github.jonpereiradev.dynamic.jpa.internal.QueryInspect;
import com.github.jonpereiradev.dynamic.jpa.internal.QueryProcessorImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class DynamicQueryValueImpl implements DynamicQueryValue {

    private final QueryInspect selectQuery;
    private final QueryInspect countQuery;
    private final Class<?> entityClass;
    private final Class<?> repositoryInterface;

    private final Map<String, QueryExpression> joins;
    private final Map<String, QueryExpression> filters;

    DynamicQueryValueImpl(String selectQuery, String countQuery, Class<?> entityClass, Class<?> repositoryInterface) {
        this.selectQuery = QueryProcessorImpl.process(selectQuery);
        this.countQuery = QueryProcessorImpl.process(countQuery);
        this.entityClass = entityClass;
        this.repositoryInterface = repositoryInterface;
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
    public QueryInspect getSelectQuery() {
        return selectQuery;
    }

    @Override
    public QueryInspect getCountQuery() {
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

    @Override
    public Class<?> getEntityClass() {
        return null;
    }

    @Override
    public Class<?> getRepositoryInterface() {
        return null;
    }

}
