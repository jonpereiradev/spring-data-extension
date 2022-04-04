package com.github.jonpereiradev.dynamic.jpa.query;


import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;


public final class DynamicJoinDefFactory {

    private final Class<?> repositoryInterface;

    public DynamicJoinDefFactory(RepositoryInformation information) {
        this.repositoryInterface = information.getRepositoryInterface();
    }

    public DynamicJoinDefFactory(RepositoryMetadata metadata) {
        this.repositoryInterface = metadata.getRepositoryInterface();
    }

    public DynamicJoinDef createRef() {
        DynamicJoinDef joinDef = new DynamicJoinDef();

        if (repositoryInterface.isAnnotationPresent(DynamicJoins.class) || repositoryInterface.isAnnotationPresent(DynamicJoin.class)) {
            DynamicJoin[] annotations = repositoryInterface.getAnnotationsByType(DynamicJoin.class);

            for (DynamicJoin annotation : annotations) {
                joinDef.put(annotation.query(), annotation.named());
            }
        }

        return joinDef;
    }

}
