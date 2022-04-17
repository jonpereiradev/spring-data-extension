package com.github.jonpereiradev.dynamic.jpa.internal.expression;


import com.github.jonpereiradev.dynamic.jpa.repository.DynamicQueryMatchers;

import java.util.Objects;
import java.util.function.Function;


/**
 * Interface para integrar com o QueryBuilder, onde permite o mapeamento de restrições por uma chave e expressão de
 * SQL.
 */
final class QueryExpressionImpl implements QueryExpression {

    private final String key;
    private final String binding;
    private final String clause;
    private final Function<Object, ?> matcher;
    private final boolean feature;

    QueryExpressionImpl(String key, String clause, Function<Object, ?> matcher) {
        this.key = key;
        this.binding = key.substring(key.indexOf(".") + 1);
        this.clause = clause;
        this.matcher = matcher;
        this.feature = false;
    }

    QueryExpressionImpl(String key, String clause, boolean feature) {
        this.key = key;
        this.binding = key.substring(key.indexOf(".") + 1);
        this.clause = clause;
        this.matcher = DynamicQueryMatchers::toBoolean;
        this.feature = feature;
    }

    @Override
    public String getKey() {
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
