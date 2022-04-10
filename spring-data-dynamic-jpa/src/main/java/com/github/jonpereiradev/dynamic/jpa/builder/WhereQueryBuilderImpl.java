package com.github.jonpereiradev.dynamic.jpa.builder;

import com.github.jonpereiradev.dynamic.jpa.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.QueryInspect;

public class WhereQueryBuilderImpl implements WhereQueryBuilder {

    private final StringBuilder internal;
    private final QueryInspect inspect;

    public WhereQueryBuilderImpl(StringBuilder internal, QueryInspect inspect) {
        this.internal = internal;
        this.inspect = inspect;
    }

    @Override
    public WhereQueryBuilder and(QueryExpression expression) {
        internal.append(" ").append(expression.getExpression());
        return this;
    }

    @Override
    public OrderByQueryBuilder order() {
        return new OrderByQueryBuilderImpl(new StringBuilder(internal), inspect);
    }

    @Override
    public String getQuery() {
        return internal.toString();
    }

}
