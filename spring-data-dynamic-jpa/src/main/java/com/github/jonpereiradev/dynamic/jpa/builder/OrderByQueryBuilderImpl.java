package com.github.jonpereiradev.dynamic.jpa.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.QueryInspect;

public class OrderByQueryBuilderImpl implements OrderByQueryBuilder {

    private final StringBuilder internal;
    private final QueryInspect inspect;

    public OrderByQueryBuilderImpl(StringBuilder internal, QueryInspect inspect) {
        this.internal = internal;
        this.inspect = inspect;
    }

    @Override
    public String getQuery() {
        return internal.toString();
    }

}
