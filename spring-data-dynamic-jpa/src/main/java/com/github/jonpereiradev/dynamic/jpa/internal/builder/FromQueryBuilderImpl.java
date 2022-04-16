package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;

public class FromQueryBuilderImpl implements FromQueryBuilder {

    private final StringBuilder internal;
    private final QueryInspectorResult result;

    public FromQueryBuilderImpl(StringBuilder internal, QueryInspectorResult result) {
        this.internal = internal;
        this.result = result;
    }

    @Override
    public FromQueryBuilder join() {
        for (int i = 0; i < result.getJoin().length; i++) {
            internal.append(" ").append(result.getJoin()[i]);
        }

        return this;
    }

    @Override
    public FromQueryBuilder join(QueryExpression queryExpression) {
        internal.append(" ").append(queryExpression.getExpression());
        return this;
    }

    @Override
    public WhereQueryBuilder where() {
        for (int i = 0; i < result.getWhere().length; i++) {
            String expression = result.getWhere()[i];

            if (i == 0) {
                internal.append(" where");

                if (expression.startsWith("and ")) {
                    expression = expression.substring(4);
                }
            }

            internal.append(" ").append(expression);
        }

        return new WhereQueryBuilderImpl(new StringBuilder(internal), result);
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
