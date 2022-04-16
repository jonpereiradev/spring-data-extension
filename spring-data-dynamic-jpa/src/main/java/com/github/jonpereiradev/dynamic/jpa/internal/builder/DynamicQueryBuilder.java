package com.github.jonpereiradev.dynamic.jpa.internal.builder;


public interface DynamicQueryBuilder {

    SelectQueryBuilder select(String alias);

    SelectQueryBuilder count(String alias);

    static DynamicQueryBuilder newInstance() {
        return new DynamicQueryBuilderImpl();
    }

}
