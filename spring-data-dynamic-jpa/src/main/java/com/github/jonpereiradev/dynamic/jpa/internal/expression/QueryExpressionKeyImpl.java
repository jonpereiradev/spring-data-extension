package com.github.jonpereiradev.dynamic.jpa.internal.expression;

public final class QueryExpressionKeyImpl implements QueryExpressionKey {

    private final String prefix;
    private final String key;

    public QueryExpressionKeyImpl(String key) {
        this.prefix = null;
        this.key = key;
    }

    public QueryExpressionKeyImpl(String prefix, String key) {
        this.prefix = prefix;
        this.key = key;
    }

    @Override
    public boolean isGlobalKey() {
        return prefix == null;
    }

    @Override
    public String getKey() {
        if (isGlobalKey()) {
            return getGlobalKey();
        }

        return prefix + "." + key;
    }

    @Override
    public String getGlobalKey() {
        return GLOBAL_PREFIX + key;
    }

}
