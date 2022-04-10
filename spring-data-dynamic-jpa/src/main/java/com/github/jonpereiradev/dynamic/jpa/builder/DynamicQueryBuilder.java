package com.github.jonpereiradev.dynamic.jpa.builder;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryValue;

public interface DynamicQueryBuilder {

    SelectQueryBuilder select(String alias);

    SelectQueryBuilder count(String alias);

    static DynamicQueryBuilder newInstance() {
        return new DynamicQueryBuilderImpl();
    }

}
