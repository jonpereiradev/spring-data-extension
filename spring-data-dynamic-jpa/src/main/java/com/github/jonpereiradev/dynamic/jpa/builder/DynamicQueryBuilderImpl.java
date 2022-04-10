package com.github.jonpereiradev.dynamic.jpa.builder;

final class DynamicQueryBuilderImpl implements DynamicQueryBuilder {

    private final StringBuilder internal;

    public DynamicQueryBuilderImpl() {
        this.internal = new StringBuilder();
    }

    @Override
    public SelectQueryBuilder select(String alias) {
        return new SelectQueryBuilderImpl(new StringBuilder(internal), alias);
    }

    @Override
    public SelectQueryBuilder count(String alias) {
        return new CountQueryBuilderImpl(new StringBuilder(internal), alias);
    }

}
