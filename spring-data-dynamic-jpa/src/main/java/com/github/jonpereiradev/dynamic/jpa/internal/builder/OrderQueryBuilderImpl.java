package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;

public class OrderQueryBuilderImpl implements OrderQueryBuilder {

    private final StringBuilder internal;

    public OrderQueryBuilderImpl(StringBuilder internal) {
        this.internal = internal;
    }

    @Override
    public OrderQueryBuilder by(QueryExpression expression) {
        if (!internal.toString().contains("order by")) {
            internal.append("order by");
            internal.append(" ").append(expression.getExpression());
        } else {
            internal.append(", ").append(expression.getExpression());
        }

        return this;
    }

    @Override
    public String toString() {
        return internal.toString().trim();
    }

}
