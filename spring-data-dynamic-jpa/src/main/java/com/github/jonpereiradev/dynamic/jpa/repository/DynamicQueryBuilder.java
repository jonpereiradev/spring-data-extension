package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.Restriction;
import com.github.jonpereiradev.dynamic.jpa.query.DynamicFilterDef;
import com.github.jonpereiradev.dynamic.jpa.query.DynamicQuery;
import com.github.jonpereiradev.dynamic.jpa.query.DynamicQueryDef;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


final class DynamicQueryBuilder {

    private static final String BLANK_SPACE = " ";
    private static final int INITIAL_QUERY_CAPACITY = 150;

    private final StringBuilder internal;
    private final DynamicQueryDef queryDef;

    DynamicQueryBuilder(DynamicQueryDef queryDef) {
        this.internal = new StringBuilder(INITIAL_QUERY_CAPACITY);
        this.queryDef = queryDef;
    }

    DynamicQueryBuilder select() {
        internal.append(queryDef.getQuery());
        return this;
    }

    DynamicQueryBuilder count() {
        internal.append(queryDef.getCountQuery());
        return this;
    }

    DynamicQueryBuilder join(DynamicQuery dynamicQuery) {
        Map<String, List<Restriction>> joins = queryDef.getJoinDef().getJoins();

        joins.entrySet()
            .stream()
            .filter(o -> dynamicQuery.isPresent(o.getKey()))
            .forEach(o -> o.getValue().forEach(join -> internal.append(BLANK_SPACE).append(join.getClause())));

        return this;
    }

    DynamicQueryBuilder where(DynamicQuery dynamicQuery, JpaQueryMethod queryMethod) {
        return where(dynamicQuery, queryMethod.getName());
    }

    DynamicQueryBuilder where(DynamicQuery dynamicQuery, String prefix) {
        internal.append(" where 1 = 1");

        DynamicFilterDef filterDef = queryDef.getFilterDef();
        FilterDefNameResolver resolver = new FilterDefNameResolver(filterDef);

        dynamicQuery.getParameters().forEach((key, value) -> {
            String keyName = resolver.resolve(key, prefix);
            Optional<Restriction> restriction = filterDef.get(keyName);
            restriction.ifPresent(o -> internal.append(BLANK_SPACE).append(o.getClause()));
        });

        return this;
    }

    DynamicQueryBuilder sorted(DynamicQuery dynamicQuery) {
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
