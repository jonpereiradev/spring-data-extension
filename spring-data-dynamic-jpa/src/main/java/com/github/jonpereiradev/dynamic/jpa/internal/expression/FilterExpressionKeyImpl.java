package com.github.jonpereiradev.dynamic.jpa.internal.expression;

import java.util.Objects;

public final class FilterExpressionKeyImpl implements QueryExpressionKey {

    private final String prefix;
    private final String value;

    public FilterExpressionKeyImpl(String value) {
        this.prefix = "filter.clazz";
        this.value = value;
    }

    public FilterExpressionKeyImpl(String prefix, String value) {
        this.prefix = "filter." + prefix;
        this.value = value;
    }

    @Override
    public boolean isGlobal() {
        return prefix.equals("filter.clazz");
    }

    @Override
    public String getValue() {
        return prefix + "." + value;
    }

    @Override
    public String getGlobalValue() {
        return "filter.clazz." + value;
    }

    @Override
    public String toString() {
        return "FilterExpressionKeyImpl{" +
            "prefix='" + prefix + '\'' +
            ", value='" + value + '\'' +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterExpressionKeyImpl that = (FilterExpressionKeyImpl) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
