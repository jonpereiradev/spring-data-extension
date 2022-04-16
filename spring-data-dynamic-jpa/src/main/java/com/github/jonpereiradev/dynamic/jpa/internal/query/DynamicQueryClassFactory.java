package com.github.jonpereiradev.dynamic.jpa.internal.query;


import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionFilterFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionJoinFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.builder.DynamicQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.core.RepositoryMetadata;


final class DynamicQueryClassFactory implements DynamicQueryFactory {

    private final Logger logger = LoggerFactory.getLogger(DynamicQueryClassFactory.class);

    private final RepositoryMetadata metadata;
    private final QueryInspector inspector;

    DynamicQueryClassFactory(RepositoryMetadata metadata) {
        this.metadata = metadata;
        this.inspector = QueryInspectorFactory.newInspector();
    }

    @Override
    public DynamicQuery newInstance() {
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
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            dynamicQuery.addJoin(expression);
        }
    }

    private void addFilterExpressions(DynamicQuery dynamicQuery) {
        QueryExpressionFactory filterFactory = new QueryExpressionFilterFactory(metadata);

        for (QueryExpression expression : filterFactory.createExpressions()) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                    "{}: Mapped default filter expression as \"{}\" binding parameter \"{}\"",
                    getRepositoryInterface().getSimpleName(),
                    expression.getExpression(),
                    expression.getBinding()
                );
            }

            dynamicQuery.addFilter(expression);
        }
    }

    private String createSelectQuery() {
        DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance();
        String aliasName = getEntityClass().getSimpleName().toLowerCase();
        return queryBuilder.select(aliasName).from(getEntityClass()).getQuery();
    }

    private String createCountQuery() {
        DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance();
        String aliasName = getEntityClass().getSimpleName().toLowerCase();

        return queryBuilder
            .count(aliasName)
            .from(getEntityClass())
            .join()
            .where()
            .order()
            .getQuery();
    }

    private Class<?> getEntityClass() {
        return metadata.getDomainType();
    }

    private Class<?> getRepositoryInterface() {
        return metadata.getRepositoryInterface();
    }

}
