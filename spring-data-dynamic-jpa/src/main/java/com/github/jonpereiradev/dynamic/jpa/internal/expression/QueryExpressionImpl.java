package com.github.jonpereiradev.dynamic.jpa.internal.expression;


import com.github.jonpereiradev.dynamic.jpa.repository.DynamicQueryMatchers;

import java.util.Objects;
import java.util.function.Function;


/**
 * Interface para integrar com o QueryBuilder, onde permite o mapeamento de restrições por uma chave e expressão de
 * SQL.
 */
public final class QueryExpressionImpl implements QueryExpression {

    private final QueryExpressionKey key;
    private final String binding;
    private final String clause;
    private final Function<Object, ?> matcher;
    private final boolean feature;

    public QueryExpressionImpl(QueryExpressionKey key, String clause, Function<Object, ?> matcher) {
        this.key = key;
        this.binding = key.getValue().substring(key.getValue().lastIndexOf(".") + 1);
        this.clause = clause;
        this.matcher = matcher;
        this.feature = false;
    }

    public QueryExpressionImpl(QueryExpressionKey key, String clause, boolean feature) {
        this.key = key;
        this.binding = key.getValue().substring(key.getValue().lastIndexOf(".") + 1);
        this.clause = clause;
        this.matcher = DynamicQueryMatchers::toBoolean;
        this.feature = feature;
    }

    @Override
    public QueryExpressionKey getKey() {
        return key;
    }

    @Override
    public String getBinding() {
        return binding;
    }

    @Override
    public String getClause() {
        return clause;
    }

    @Override
    public boolean isFeature() {
        return feature;
    }

    @Override
    public Function<Object, ?> getMatcher() {
        return matcher;
    }

    @Override
    public String toString() {
        return "QueryExpressionImpl{" +
            "key=" + key +
            ", binding='" + binding + '\'' +
            ", clause='" + clause + '\'' +
            ", feature=" + feature +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QueryExpressionImpl that = (QueryExpressionImpl) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
