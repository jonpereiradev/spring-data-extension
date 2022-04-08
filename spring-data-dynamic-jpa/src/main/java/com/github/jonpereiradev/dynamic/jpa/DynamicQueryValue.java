package com.github.jonpereiradev.dynamic.jpa;

import java.util.Optional;
import java.util.Set;

interface DynamicQueryValue {

    String getQuery();

    String getCountQuery();

    void addJoin(QueryExpression expression);

    void addFilter(QueryExpression expression);

    Optional<QueryExpression> getJoinValue(QueryExpressionKey expressionKey);

    Optional<QueryExpression> getFilterValue(QueryExpressionKey expressionKey);

    Set<QueryExpression> getJoinExpressions();

    Set<QueryExpression> getFilterExpressions();

}
