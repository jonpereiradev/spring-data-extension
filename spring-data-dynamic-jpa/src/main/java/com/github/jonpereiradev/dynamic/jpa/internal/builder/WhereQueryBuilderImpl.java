package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;

public class WhereQueryBuilderImpl implements WhereQueryBuilder {

    private final StringBuilder internal;
    private final QueryInspectorResult result;

    public WhereQueryBuilderImpl(StringBuilder internal, QueryInspectorResult result) {
        this.internal = internal;
        this.result = result;
    }

    @Override
    public WhereQueryBuilder and(QueryExpression expression) {
        String value = expression.getClause();

        if (!internal.toString().contains("where")) {
            internal.append(" where");

            if (value.startsWith("and ")) {
                value = value.substring(4);
            }
        }

        internal.append(" ").append(value);

        return this;
    }

    @Override
    public OrderQueryBuilder order() {
        for (int i = 0; i < result.getOrderBy().length; i++) {
            if (i == 0) {
                internal.append("order by");
            }

            if (i > 0) {
                internal.append(",");
            }

            internal.append(" ").append(result.getOrderBy()[i]);
        }

        return new OrderQueryBuilderImpl(new StringBuilder(internal));
    }

    @Override
    public String toString() {
        return internal.toString().trim();
    }

}
