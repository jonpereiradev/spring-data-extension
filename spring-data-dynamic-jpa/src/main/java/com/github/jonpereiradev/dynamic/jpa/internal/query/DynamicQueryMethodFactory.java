package com.github.jonpereiradev.dynamic.jpa.internal.query;


import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotationReader;
import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotationReaderFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.builder.DynamicQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionFilterFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionJoinFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;


final class DynamicQueryMethodFactory implements DynamicQueryFactory {

    private final Logger logger = LoggerFactory.getLogger(DynamicQueryMethodFactory.class);

    private final RepositoryMetadata metadata;
    private final Method method;

    DynamicQueryMethodFactory(RepositoryMetadata metadata, Method method) {
        this.metadata = metadata;
        this.method = method;
    }

    @Override
    public DynamicQuery newInstance() {
        String selectQuery = createSelectQuery(method);
        String countQuery = createCountQuery(method);

        DynamicQuery dynamicQuery = new DynamicQueryImpl(
            selectQuery,
            countQuery,
            metadata.getDomainType(),
            metadata.getRepositoryInterface()
        );

        String logKeyName = metadata.getRepositoryInterface().getSimpleName() + "." + method.getName();

        if (logger.isDebugEnabled()) {
            logger.debug(
                "{}: Mapped query as \"{}\" and count query as \"{}\"",
                logKeyName,
                selectQuery,
                countQuery
            );
        }

        addJoinExpressions(method, dynamicQuery, logKeyName);
        addFilterExpressions(method, dynamicQuery, logKeyName);

        return dynamicQuery;
    }

    private void addJoinExpressions(Method method, DynamicQuery dynamicQuery, String logKeyName) {
        QueryExpressionFactory joinFactory = new QueryExpressionJoinFactory(metadata);

        for (QueryExpression expression : joinFactory.createExpressions(method)) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped join expression as \"{}\" binding parameter \"{}\"",
                    logKeyName,
                    expression.getClause(),
                    expression.getBinding()
                );
            }

            dynamicQuery.addExpression(expression);
        }
    }

    private void addFilterExpressions(Method method, DynamicQuery dynamicQuery, String logKeyName) {
        JpaAnnotationReaderFactory factory = new JpaAnnotationReaderFactory();
        JpaAnnotationReader reader = factory.createReader(metadata.getDomainType());
        QueryExpressionFactory filterFactory = new QueryExpressionFilterFactory(metadata, reader);

        for (QueryExpression expression : filterFactory.createExpressions(method)) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped filter expression as \"{}\" binding parameter \"{}\"",
                    logKeyName,
                    expression.getClause(),
                    expression.getBinding()
                );
            }

            dynamicQuery.addExpression(expression);
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

}
