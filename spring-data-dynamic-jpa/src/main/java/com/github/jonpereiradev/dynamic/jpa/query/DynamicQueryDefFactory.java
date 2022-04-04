package com.github.jonpereiradev.dynamic.jpa.query;


import org.apache.commons.lang3.StringUtils;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;


public final class DynamicQueryDefFactory {

    private final Class<?> entityClass;
    private final Class<?> repositoryInterface;
    private final DynamicJoinDefFactory joinDefFactory;
    private final DynamicFilterDefFactory filterDefFactory;

    public DynamicQueryDefFactory(RepositoryInformation information) {
        this.entityClass = information.getDomainType();
        this.repositoryInterface = information.getRepositoryInterface();
        this.joinDefFactory = new DynamicJoinDefFactory(information);
        this.filterDefFactory = new DynamicFilterDefFactory(information);
    }

    public DynamicQueryDefFactory(RepositoryMetadata metadata) {
        this.entityClass = metadata.getDomainType();
        this.repositoryInterface = metadata.getRepositoryInterface();
        this.joinDefFactory = new DynamicJoinDefFactory(metadata);
        this.filterDefFactory = new DynamicFilterDefFactory(metadata);
    }

    public DynamicQueryDef createRef() {
        String query = createQuery();
        String countQuery = createCountQuery();

        DynamicJoinDef joinDef = joinDefFactory.createRef();
        DynamicFilterDef filterDef = filterDefFactory.createRef();

        return new DynamicQueryDef(query, countQuery, joinDef, filterDef);
    }

    private String createQuery() {
        if (repositoryInterface.isAnnotationPresent(DynamicSelect.class)) {
            DynamicSelect dynamicSelect = repositoryInterface.getAnnotation(DynamicSelect.class);

            if (StringUtils.isNotBlank(dynamicSelect.query())) {
                int indexOf = dynamicSelect.query().indexOf("where");
                return indexOf == -1 ? dynamicSelect.query() : dynamicSelect.query().substring(0, indexOf);
            } else if (StringUtils.isNotBlank(dynamicSelect.alias())) {
                String alias = dynamicSelect.alias();
                String entityName = entityClass.getSimpleName();
                return "select " + alias + " from " + entityName + " " + alias;
            }
        }

        return "select o from " + entityClass.getSimpleName() + " o";
    }

    private String createCountQuery() {
        if (repositoryInterface.isAnnotationPresent(DynamicSelect.class)) {
            DynamicSelect dynamicSelect = repositoryInterface.getAnnotation(DynamicSelect.class);

            if (StringUtils.isNotBlank(dynamicSelect.query())) {
                int indexOf = dynamicSelect.query().indexOf("where");
                String query = indexOf == -1 ? dynamicSelect.query() : dynamicSelect.query().substring(0, indexOf);
                boolean distinct = query.contains("distinct");
                String countQuery = query.replaceAll("select .* from ", "");

                if (distinct) {
                    return "select count(distinct o.id) from " + countQuery.replaceAll("fetch", "");
                } else {
                    return "select count(1) from " + countQuery.replaceAll("fetch", "");
                }
            } else if (StringUtils.isNotBlank(dynamicSelect.alias())) {
                String entityName = entityClass.getSimpleName();
                return "select count(1) from " + entityName;
            }
        }

        return "select count(1) from " + entityClass.getSimpleName() + " o";
    }

}
