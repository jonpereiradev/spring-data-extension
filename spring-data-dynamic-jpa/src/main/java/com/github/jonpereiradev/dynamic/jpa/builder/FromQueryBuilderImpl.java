package com.github.jonpereiradev.dynamic.jpa.builder;

import com.github.jonpereiradev.dynamic.jpa.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.QueryInspect;

public class FromQueryBuilderImpl implements FromQueryBuilder {

    private final StringBuilder internal;
    private final QueryInspect inspect;

    public FromQueryBuilderImpl(StringBuilder internal, QueryInspect inspect) {
        this.internal = internal;
        this.inspect = inspect;
    }

    @Override
    public FromQueryBuilder join() {
        for (int i = 0; i < inspect.getJoins().length; i++) {
            internal.append(inspect.getJoins()[i]);

            if (i < inspect.getJoins().length - 1) {
                internal.append(" ");
            }
        }

        return this;
    }

    @Override
    public FromQueryBuilder join(QueryExpression queryExpression) {
        internal.append(queryExpression.getExpression());
        return this;
    }

    @Override
    public WhereQueryBuilder where() {
        if (inspect.getWhere().length > 0) {
            internal.append(" where ");
        } else {
            internal.append(" where 1 = 1 ");
        }

        for (int i = 0; i < inspect.getWhere().length; i++) {
            internal.append(inspect.getWhere()[i]);

            if (i < inspect.getWhere().length - 1) {
                internal.append(" ");
            }
        }

        return new WhereQueryBuilderImpl(new StringBuilder(internal), inspect);
    }

    @Override
    public OrderByQueryBuilder orderBy() {
        return new OrderByQueryBuilderImpl(new StringBuilder(internal), inspect);
    }

    @Override
    public String getQuery() {
        return internal.toString();
    }
}
