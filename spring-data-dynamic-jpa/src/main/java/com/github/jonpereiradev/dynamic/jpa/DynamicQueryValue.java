package com.github.jonpereiradev.dynamic.jpa;

import com.github.jonpereiradev.dynamic.jpa.internal.QueryInspect;

import java.util.Optional;
import java.util.Set;

public interface DynamicQueryValue {

    QueryInspect getSelectQuery();

    QueryInspect getCountQuery();

    void addJoin(QueryExpression expression);

    void addFilter(QueryExpression expression);

    Optional<QueryExpression> getJoinValue(QueryExpressionKey expressionKey);

    Optional<QueryExpression> getFilterValue(QueryExpressionKey expressionKey);

    Set<QueryExpression> getJoinExpressions();

    Set<QueryExpression> getFilterExpressions();

    Class<?> getEntityClass();

    Class<?> getRepositoryInterface();

}
