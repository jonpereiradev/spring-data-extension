package com.github.jonpereiradev.dynamic.jpa;

final class QueryExpressionKey {

    private final String prefix;
    private final String key;

    public QueryExpressionKey(String key) {
        this.prefix = null;
        this.key = key;
    }

    public QueryExpressionKey(String prefix, String key) {
        this.prefix = prefix;
        this.key = key;
    }

    public boolean isGlobalKey() {
        return prefix == null;
    }

    public String getKey() {
        if (isGlobalKey()) {
            return getGlobalKey();
        }

        return prefix + "." + key;
    }

    public String getGlobalKey() {
        return "clazz." + key;
    }

}
