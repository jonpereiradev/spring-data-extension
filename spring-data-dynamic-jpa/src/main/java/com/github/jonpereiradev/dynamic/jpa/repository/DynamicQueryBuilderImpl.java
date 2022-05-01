package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.JoinExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKey;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.FilterExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.query.DynamicQuery;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;


final class DynamicQueryBuilderImpl {

    private static final String BLANK_SPACE = " ";
    private static final int INITIAL_QUERY_CAPACITY = 150;

    private final StringBuilder internal;
    private final DynamicQuery queryValue;

    DynamicQueryBuilderImpl(DynamicQuery queryValue) {
        this.internal = new StringBuilder(INITIAL_QUERY_CAPACITY);
        this.queryValue = queryValue;
    }

    DynamicQueryBuilderImpl select() {
        internal.append(queryValue.getSelectQuery());
        return this;
    }

    DynamicQueryBuilderImpl count() {
        internal.append(queryValue.getCountQuery());
        return this;
    }

    DynamicQueryBuilderImpl join(DynamicQueryParams dynamicQuery) {
        dynamicQuery.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new JoinExpressionKeyImpl(key);
            Optional<QueryExpression> joinValue = queryValue.getExpression(expressionKey);
            joinValue.ifPresent(query -> internal.append(BLANK_SPACE).append(query.getClause()));
        });

        return this;
    }

    DynamicQueryBuilderImpl where(DynamicQueryParams params, JpaQueryMethod queryMethod) {
        return where(params, queryMethod.getName());
    }

    DynamicQueryBuilderImpl where(DynamicQueryParams params, String prefix) {
        internal.append(" where 1 = 1");

        params.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new FilterExpressionKeyImpl(prefix, key);
            Optional<QueryExpression> filterValue = queryValue.getExpression(expressionKey);
            filterValue.ifPresent(query -> internal.append(BLANK_SPACE).append(query.getClause()));
        });

        return this;
    }

    DynamicQueryBuilderImpl sorted(DynamicQueryParams dynamicQuery) {
        Sort sort = dynamicQuery.getSort();

        if (sort != null) {
            Iterator<Sort.Order> iterator = sort.iterator();

            if (iterator.hasNext()) {
                Sort.Order next = iterator.next();
                internal.append("order by o.").append(next.getProperty()).append(BLANK_SPACE).append(next.getDirection().name());
            }

            while (iterator.hasNext()) {
                Sort.Order next = iterator.next();
                internal.append(", o.").append(next.getProperty()).append(BLANK_SPACE).append(next.getDirection().name());
            }
        }

        return this;
    }

    String query() {
        return internal.toString();
    }

    @Override
    public String toString() {
        return internal.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(internal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DynamicQueryBuilderImpl that = (DynamicQueryBuilderImpl) o;
        return toString().equals(that.toString());
    }
}
