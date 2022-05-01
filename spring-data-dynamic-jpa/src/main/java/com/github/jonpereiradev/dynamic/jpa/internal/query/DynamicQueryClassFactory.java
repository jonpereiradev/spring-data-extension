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
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;


final class DynamicQueryClassFactory implements DynamicQueryFactory {

    private final Logger logger = LoggerFactory.getLogger(DynamicQueryClassFactory.class);

    private final RepositoryMetadata metadata;

    DynamicQueryClassFactory(RepositoryMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public DynamicQuery newInstance() {
        for (Method declaredMethod : metadata.getRepositoryInterface().getDeclaredMethods()) {
            DynamicQueryFactory methodFactory = new DynamicQueryMethodFactory(metadata, declaredMethod);
            methodFactory.newInstance();
        }

        String selectQuery = createSelectQuery();
        String countQuery = createCountQuery();
        DynamicQuery dynamicQuery = new DynamicQueryImpl(selectQuery, countQuery, getEntityClass(), getRepositoryInterface());

        if (logger.isDebugEnabled()) {
            logger.debug(
                "{}: Mapped default query as \"{}\" and default count query as \"{}\"",
                metadata.getRepositoryInterface().getSimpleName(),
                selectQuery,
                countQuery
            );
        }

        addJoinExpressions(dynamicQuery);
        addFilterExpressions(dynamicQuery);

        return dynamicQuery;
    }

    private void addJoinExpressions(DynamicQuery dynamicQuery) {
        QueryExpressionFactory joinFactory = new QueryExpressionJoinFactory(metadata);

        for (QueryExpression expression : joinFactory.createExpressions()) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped default join expression as \"{}\" binding parameter \"{}\"",
                    getRepositoryInterface().getSimpleName(),
                    expression.getClause(),
                    expression.getBinding()
                );
            }

            dynamicQuery.addExpression(expression);
        }
    }

    private void addFilterExpressions(DynamicQuery dynamicQuery) {
        JpaAnnotationReaderFactory factory = new JpaAnnotationReaderFactory();
        JpaAnnotationReader reader = factory.createReader(metadata.getDomainType());
        QueryExpressionFactory filterFactory = new QueryExpressionFilterFactory(metadata, reader);

        for (QueryExpression expression : filterFactory.createExpressions()) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped default filter expression as \"{}\" binding parameter \"{}\"",
                    getRepositoryInterface().getSimpleName(),
                    expression.getClause(),
                    expression.getBinding()
                );
            }

            dynamicQuery.addExpression(expression);
        }
    }

    private String createSelectQuery() {
        DynamicQueryBuilder builder = DynamicQueryBuilder.newInstance(getEntityClass());
        return builder.select().from().join().where().order().toString();
    }

    private String createCountQuery() {
        DynamicQueryBuilder builder = DynamicQueryBuilder.newInstance(getEntityClass());
        return builder.count().from().join().where().order().toString();
    }

    private Class<?> getEntityClass() {
        return metadata.getDomainType();
    }

    private Class<?> getRepositoryInterface() {
        return metadata.getRepositoryInterface();
    }

}
