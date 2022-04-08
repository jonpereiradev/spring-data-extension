package com.github.jonpereiradev.dynamic.jpa;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;


final class DynamicQueryBuilder {

    private static final String BLANK_SPACE = " ";
    private static final int INITIAL_QUERY_CAPACITY = 150;

    private final StringBuilder internal;
    private final DynamicQueryValue queryValue;

    DynamicQueryBuilder(DynamicQueryValue queryValue) {
        this.internal = new StringBuilder(INITIAL_QUERY_CAPACITY);
        this.queryValue = queryValue;
    }

    DynamicQueryBuilder select() {
        internal.append(queryValue.getQuery());
        return this;
    }

    DynamicQueryBuilder count() {
        internal.append(queryValue.getCountQuery());
        return this;
    }

    DynamicQueryBuilder join(DynamicQueryParams dynamicQuery) {
        dynamicQuery.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new QueryExpressionKey(key);
            Optional<QueryExpression> joinValue = queryValue.getJoinValue(expressionKey);
            joinValue.ifPresent(query -> internal.append(BLANK_SPACE).append(query.getExpression()));
        });

        return this;
    }

    DynamicQueryBuilder where(DynamicQueryParams dynamicQuery, JpaQueryMethod queryMethod) {
        return where(dynamicQuery, queryMethod.getName());
    }

    DynamicQueryBuilder where(DynamicQueryParams dynamicQuery, String prefix) {
        internal.append(" where 1 = 1");

        dynamicQuery.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new QueryExpressionKey(prefix, key);
            Optional<QueryExpression> filterValue = queryValue.getFilterValue(expressionKey);
            filterValue.ifPresent(query -> internal.append(BLANK_SPACE).append(query.getExpression()));
        });

        return this;
    }

    DynamicQueryBuilder sorted(DynamicQueryParams dynamicQuery) {
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
        DynamicQueryBuilder that = (DynamicQueryBuilder) o;
        return toString().equals(that.toString());
    }
}
