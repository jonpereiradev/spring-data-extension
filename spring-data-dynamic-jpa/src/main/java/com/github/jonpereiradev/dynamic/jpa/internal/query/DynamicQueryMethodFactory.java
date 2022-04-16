package com.github.jonpereiradev.dynamic.jpa.internal.query;


import com.github.jonpereiradev.dynamic.jpa.internal.builder.DynamicQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionFilterFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionJoinFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;


final class DynamicQueryMethodFactory implements DynamicQueryFactory {

    private final Logger logger = LoggerFactory.getLogger(DynamicQueryMethodFactory.class);

    private final RepositoryMetadata metadata;
    private final Method method;
    private final QueryInspector inspector;

    DynamicQueryMethodFactory(RepositoryMetadata metadata, Method method) {
        this.metadata = metadata;
        this.method = method;
        this.inspector = QueryInspectorFactory.newInspector();
    }

    @Override
    public DynamicQuery newInstance() {
        String selectQuery = createSelectQuery(method);
        String countQuery = createCountQuery(method);

        DynamicQuery queryValue = new DynamicQueryImpl(
            selectQuery,
            countQuery,
            metadata.getDomainType(),
            metadata.getRepositoryInterface()
        );

        String key = metadata.getRepositoryInterface().getSimpleName() + "." + method.getName();

        if (logger.isDebugEnabled()) {
            logger.debug(
                "{}: Mapped query as \"{}\" and count query as \"{}\"",
                key,
                selectQuery,
                countQuery
            );
        }

//        if (INSTANCES.containsKey(metadata.getRepositoryInterface().getName())) {
//            DynamicQuery value = INSTANCES.get(metadata.getRepositoryInterface().getName());
//            value.getJoinExpressions().forEach(queryValue::addJoin);
//            value.getFilterExpressions().forEach(queryValue::addFilter);
//        }

        addJoinExpressions(method, queryValue, key);
        addFilterExpressions(method, queryValue, key);

//        INSTANCES.put(key, queryValue);

        return queryValue;
    }

    private void addJoinExpressions(Method method, DynamicQuery dynamicQuery, String logKeyName) {
        QueryExpressionFactory joinFactory = new QueryExpressionJoinFactory(metadata);
        String aliasName = dynamicQuery.getSelectQuery().getFrom()[0].getAliasName();

        for (QueryExpression expression : joinFactory.createExpressions(aliasName)) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped join expression as \"{}\" binding parameter \"{}\"",
                    logKeyName,
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            dynamicQuery.addJoin(expression);
        }

        for (QueryExpression expression : joinFactory.createExpressions(method)) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped join expression as \"{}\" binding parameter \"{}\"",
                    logKeyName,
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            dynamicQuery.addJoin(expression);
        }
    }

    private void addFilterExpressions(Method method, DynamicQuery dynamicQuery, String logKeyName) {
        QueryExpressionFactory filterFactory = new QueryExpressionFilterFactory(metadata);
        String aliasName = dynamicQuery.getSelectQuery().getFrom()[0].getAliasName();

        for (QueryExpression expression : filterFactory.createExpressions(aliasName)) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped filter expression as \"{}\" binding parameter \"{}\"",
                    logKeyName,
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            dynamicQuery.addFilter(expression);
        }

        for (QueryExpression expression : filterFactory.createExpressions(method)) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped filter expression as \"{}\" binding parameter \"{}\"",
                    logKeyName,
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            dynamicQuery.addFilter(expression);
        }
    }

    private String createSelectQuery() {
        DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance(getEntityClass());
        return queryBuilder.select().from().toString();
    }

    private String createCountQuery() {
        DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance(getEntityClass());

        return queryBuilder
            .count()
            .from()
            .join()
            .where()
            .order()
            .toString();
    }

    private String createSelectQuery(Method method) {
        DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance(getEntityClass());
        Query query = method.getAnnotation(Query.class);

        if (query == null) {
            return createSelectQuery();
        }

        return queryBuilder.select(query).from().join().where().order().toString();
    }

    private String createCountQuery(Method method) {
        DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance(getEntityClass());
        Query query = method.getAnnotation(Query.class);

        if (query == null) {
            return createCountQuery();
        }

        return queryBuilder.count(query).from().join().where().toString();
    }

    private Class<?> getEntityClass() {
        return metadata.getDomainType();
    }

    private Class<?> getRepositoryInterface() {
        return metadata.getRepositoryInterface();
    }

}
