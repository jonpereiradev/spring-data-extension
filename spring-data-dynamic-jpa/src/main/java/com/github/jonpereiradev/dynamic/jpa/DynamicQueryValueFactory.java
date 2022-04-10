package com.github.jonpereiradev.dynamic.jpa;


import com.github.jonpereiradev.dynamic.jpa.builder.DynamicQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


final class DynamicQueryValueFactory {

    private static final Map<String, DynamicQueryValue> INSTANCES = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(DynamicJpaRepositoryFactory.class);

    private final Class<?> entityClass;
    private final Class<?> repositoryInterface;
    private final RepositoryMetadata metadata;

    DynamicQueryValueFactory(RepositoryMetadata metadata) {
        this.entityClass = metadata.getDomainType();
        this.repositoryInterface = metadata.getRepositoryInterface();
        this.metadata = metadata;
    }

    DynamicQueryValue newDynamicQueryValue() {
        String selectQuery = createSelectQuery();
        String countQuery = createCountQuery();
        DynamicQueryValue dynamicQueryValue = new DynamicQueryValueImpl(selectQuery, countQuery, entityClass, repositoryInterface);

        if (logger.isDebugEnabled()) {
            logger.debug(
                "{}: Mapped default query as \"{}\" and default count query as \"{}\"",
                repositoryInterface.getSimpleName(),
                selectQuery,
                countQuery
            );
        }

        addJoinExpressions(dynamicQueryValue);
        addFilterExpressions(dynamicQueryValue);

        INSTANCES.put(repositoryInterface.getName(), dynamicQueryValue);

        return dynamicQueryValue;
    }

    private void addJoinExpressions(DynamicQueryValue dynamicQueryValue) {
        QueryExpressionFactory joinFactory = new QueryExpressionJoinFactory(metadata);

        for (QueryExpression expression : joinFactory.createExpressions()) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped default join expression as \"{}\" binding parameter \"{}\"",
                    repositoryInterface.getSimpleName(),
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            dynamicQueryValue.addJoin(expression);
        }
    }

    private void addFilterExpressions(DynamicQueryValue dynamicQueryValue) {
        QueryExpressionFactory filterFactory = new QueryExpressionFilterFactory(metadata);

        for (QueryExpression expression : filterFactory.createExpressions()) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped default filter expression as \"{}\" binding parameter \"{}\"",
                    repositoryInterface.getSimpleName(),
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            dynamicQueryValue.addFilter(expression);
        }
    }

    DynamicQueryValue newDynamicQueryValue(Method method) {
        String selectQuery = createSelectQuery(method);
        String countQuery = createCountQuery(method);
        DynamicQueryValue queryValue = new DynamicQueryValueImpl(selectQuery, countQuery, entityClass, repositoryInterface);
        String key = repositoryInterface.getSimpleName() + "." + method.getName();

        if (logger.isDebugEnabled()) {
            logger.debug(
                "{}: Mapped query as \"{}\" and count query as \"{}\"",
                key,
                selectQuery,
                countQuery
            );
        }

        if (INSTANCES.containsKey(repositoryInterface.getName())) {
            DynamicQueryValue value = INSTANCES.get(repositoryInterface.getName());
            value.getJoinExpressions().forEach(queryValue::addJoin);
            value.getFilterExpressions().forEach(queryValue::addFilter);
        }

        addJoinExpressions(method, queryValue, key);
        addFilterExpressions(method, queryValue, key);

        INSTANCES.put(key, queryValue);

        return queryValue;
    }

    private void addJoinExpressions(Method method, DynamicQueryValue queryValue, String logKeyName) {
        QueryExpressionFactory joinFactory = new QueryExpressionJoinFactory(metadata);

        for (QueryExpression expression : joinFactory.createExpressions(method)) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped join expression as \"{}\" binding parameter \"{}\"",
                    logKeyName,
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            queryValue.addJoin(expression);
        }
    }

    private void addFilterExpressions(Method method, DynamicQueryValue queryValue, String logKeyName) {
        QueryExpressionFactory filterFactory = new QueryExpressionFilterFactory(metadata);

        for (QueryExpression expression : filterFactory.createExpressions(method)) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped filter expression as \"{}\" binding parameter \"{}\"",
                    logKeyName,
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            queryValue.addFilter(expression);
        }
    }

    private String createSelectQuery() {
        DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance();
        return queryBuilder.select(entityClass.getSimpleName().toLowerCase()).from(entityClass).getQuery();
    }

    private String createCountQuery() {
        DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance();
        return queryBuilder.count(entityClass.getSimpleName().toLowerCase()).from(entityClass).join().where().order().getQuery();
    }

    private String createSelectQuery(Method method) {
        DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance();
        Query query = method.getAnnotation(Query.class);

        if (query == null) {
            return createSelectQuery();
        }

        return queryBuilder.select(null).from(entityClass, query).join().where().order().getQuery();
    }

    private String createCountQuery(Method method) {
        DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance();
        Query query = method.getAnnotation(Query.class);

        if (query == null) {
            return createCountQuery();
        }

        return queryBuilder.count(null).from(entityClass, query).join().where().getQuery();
    }

}
