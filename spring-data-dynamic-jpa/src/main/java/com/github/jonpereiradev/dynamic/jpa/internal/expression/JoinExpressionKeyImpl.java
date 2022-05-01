package com.github.jonpereiradev.dynamic.jpa.internal.expression;

import java.util.Objects;

public final class JoinExpressionKeyImpl implements QueryExpressionKey {

    private final String prefix;
    private final String value;

    public JoinExpressionKeyImpl(String value) {
        this.prefix = "join.clazz";
        this.value = value;
    }

    public JoinExpressionKeyImpl(String prefix, String value) {
        this.prefix = "join." + prefix;
        this.value = value;
    }

    @Override
    public boolean isGlobal() {
        return prefix.equals("join.clazz");
    }

    @Override
    public String getValue() {
        return prefix + "." + value;
    }

    @Override
    public String getGlobalValue() {
        return "join.clazz." + value;
    }

    @Override
    public String toString() {
        return "JoinExpressionKeyImpl{" +
            "prefix='" + prefix + '\'' +
            ", value='" + value + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinExpressionKeyImpl that = (JoinExpressionKeyImpl) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
