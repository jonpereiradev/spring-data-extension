package com.github.jonpereiradev.dynamic.jpa;


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
    private final Class<?> repositoryClass;
    private final QueryExpressionFactory queryExpressionJoinFactory;
    private final QueryExpressionFactory queryExpressionFilterFactory;

    DynamicQueryValueFactory(RepositoryMetadata metadata) {
        this.entityClass = metadata.getDomainType();
        this.repositoryClass = metadata.getRepositoryInterface();
        this.queryExpressionJoinFactory = new QueryExpressionJoinFactory(metadata);
        this.queryExpressionFilterFactory = new QueryExpressionFilterFactory(metadata);
    }

    DynamicQueryValue create() {
        if (INSTANCES.containsKey(repositoryClass.getName())) {
            return INSTANCES.get(repositoryClass.getName());
        }

        String query = createQuery();
        String countQuery = createCountQuery();
        DynamicQueryValue dynamicQueryValue = new DynamicQueryValueImpl(query, countQuery);

        if (logger.isDebugEnabled()) {
            logger.debug(
                "{}: Mapped default query as \"{}\" and default count query as \"{}\"",
                repositoryClass.getSimpleName(),
                query,
                countQuery
            );
        }

        for (QueryExpression expression : queryExpressionJoinFactory.createExpressions()) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped default join expression as \"{}\" binding parameter \"{}\"",
                    repositoryClass.getSimpleName(),
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            dynamicQueryValue.addJoin(expression);
        }

        for (QueryExpression expression : queryExpressionFilterFactory.createExpressions()) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped default filter expression as \"{}\" binding parameter \"{}\"",
                    repositoryClass.getSimpleName(),
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            dynamicQueryValue.addFilter(expression);
        }

        INSTANCES.putIfAbsent(repositoryClass.getName(), dynamicQueryValue);

        return dynamicQueryValue;
    }

    DynamicQueryValue create(Method method) {
        String key = repositoryClass.getName() + "." + method.getName();

        if (INSTANCES.containsKey(key)) {
            return INSTANCES.get(key);
        }

        String query = createQuery(method);
        String countQuery = createCountQuery(method);
        DynamicQueryValue queryValue = new DynamicQueryValueImpl(query, countQuery);
        String logKeyName = repositoryClass.getSimpleName() + "." + method.getName();

        if (logger.isDebugEnabled()) {
            logger.debug(
                "{}: Mapped query as \"{}\" and count query as \"{}\"",
                logKeyName,
                query,
                countQuery
            );
        }

        if (INSTANCES.containsKey(repositoryClass.getName())) {
            DynamicQueryValue value = INSTANCES.get(repositoryClass.getName());
            value.getJoinExpressions().forEach(queryValue::addJoin);
            value.getFilterExpressions().forEach(queryValue::addFilter);
        }

        for (QueryExpression expression : queryExpressionJoinFactory.createExpressions(method)) {
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

        for (QueryExpression expression : queryExpressionFilterFactory.createExpressions(method)) {
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

        INSTANCES.put(key, queryValue);

        return queryValue;
    }

    private String createQuery() {
        return createQuery(null);
    }

    private String createCountQuery() {
        return createCountQuery(null);
    }

    private String createQuery(Method method) {
        Query query = null;

        if (method != null && method.isAnnotationPresent(Query.class)) {
            query = method.getAnnotation(Query.class);
        }

        if (query == null) {
            String alias = entityClass.getSimpleName().toLowerCase();
            return "select " + alias + " from " + entityClass.getSimpleName() + " " + alias;
        }

        return query.value().trim();
    }

    private String createCountQuery(Method method) {
        Query query = null;

        if (method != null && method.isAnnotationPresent(Query.class)) {
            query = method.getAnnotation(Query.class);
        }

        if (query == null) {
            return "select count(o.id) from " + entityClass.getSimpleName() + " " + entityClass.getSimpleName().toLowerCase();
        }

        String queryString = query.value();
        boolean distinct = queryString.contains("distinct");
        String countQuery = queryString.replaceAll("select .* from ", "");

        if (distinct) {
            return "select count(distinct o.id) from " + countQuery.replaceAll("fetch", "").trim();
        }

        return "select count(o.id) from " + countQuery.replaceAll("fetch", "").trim();
    }

}
