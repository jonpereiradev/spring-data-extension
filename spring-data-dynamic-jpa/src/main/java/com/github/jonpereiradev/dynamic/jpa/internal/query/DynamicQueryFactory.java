package com.github.jonpereiradev.dynamic.jpa.internal.query;


import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;

public interface DynamicQueryFactory {

    static DynamicQueryFactory newInstance(RepositoryMetadata metadata) {
        return new DynamicQueryFactoryImpl(metadata);
    }

    DynamicQuery newInstance(Method method);

}
