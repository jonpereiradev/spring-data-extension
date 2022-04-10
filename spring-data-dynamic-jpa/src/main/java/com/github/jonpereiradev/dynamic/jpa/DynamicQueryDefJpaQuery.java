package com.github.jonpereiradev.dynamic.jpa;


import com.github.jonpereiradev.dynamic.jpa.builder.DynamicQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.builder.FromQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.builder.WhereQueryBuilder;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaParametersParameterAccessor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.Optional;
import java.util.regex.Pattern;


/**
 * Responsible to handle queries for methods declared on the interface created by the developer.
 */
final class DynamicQueryDefJpaQuery extends AbstractJpaQuery {

    private static final Pattern PATTERN_IN_CLAUSE = Pattern.compile(".*\\sin\\s?\\(.*");

    private final DynamicQueryValue dynamicQuery;

    DynamicQueryDefJpaQuery(JpaQueryMethod jpaQueryMethod, EntityManager entityManager, DynamicQueryValue dynamicQuery) {
        super(jpaQueryMethod, entityManager);
        this.dynamicQuery = dynamicQuery;
        ValidateQuery.validate(this, getEntityManager().getEntityManagerFactory(), dynamicQuery, jpaQueryMethod);
    }

    @Override
    protected Query doCreateQuery(JpaParametersParameterAccessor accessor) {
        DynamicQueryBuilder selectQueryBuilder = DynamicQueryBuilder.newInstance();
        DynamicQueryParams params = (DynamicQueryParams) accessor.getValues()[0];
        String aliasName = dynamicQuery.getSelectQuery().getAliasName();

        FromQueryBuilder fromQueryBuilder = selectQueryBuilder
            .select(aliasName)
            .from(dynamicQuery.getEntityClass(), dynamicQuery.getSelectQuery())
            .join();

        addDynamicJoin(params, fromQueryBuilder);

        WhereQueryBuilder whereQueryBuilder = fromQueryBuilder.where();
        addDynamicWhere(params, whereQueryBuilder);

        String selectQuery = whereQueryBuilder.order().getQuery();
        Query query = createQueryOf(params, selectQuery);

        if (params.isPageable()) {
            createPageable(query, params);
        }

        return query;
    }

    @Override
    protected Query doCreateCountQuery(JpaParametersParameterAccessor accessor) {
        DynamicQueryBuilder countQueryBuilder = DynamicQueryBuilder.newInstance();
        DynamicQueryParams params = (DynamicQueryParams) accessor.getValues()[0];
        String aliasName = dynamicQuery.getSelectQuery().getAliasName();

        FromQueryBuilder fromQueryBuilder = countQueryBuilder
            .count(aliasName)
            .from(dynamicQuery.getEntityClass(), dynamicQuery.getSelectQuery())
            .join();

        addDynamicJoin(params, fromQueryBuilder);

        WhereQueryBuilder whereQueryBuilder = fromQueryBuilder.where();
        addDynamicWhere(params, whereQueryBuilder);

        String countQuery = whereQueryBuilder.order().getQuery();
        return createQueryOf(params, countQuery);
    }

    private Query createQueryOf(DynamicQueryParams params, String queryString) {
        Query query = getEntityManager().createQuery(queryString);

        params.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new QueryExpressionKey(getQueryMethod().getName(), key);
            Optional<QueryExpression> restriction = dynamicQuery.getFilterValue(expressionKey);
            restriction.ifPresent(o -> setQueryParameter(params, query, o));
        });

        return query;
    }

    private void setQueryParameter(DynamicQueryParams params, Query query, QueryExpression expression) {
        if (!expression.isFeature()) {
            Object transformed = expression.getMatcher().apply(params.getObject(expression.getBinding()));

            if (PATTERN_IN_CLAUSE.matcher(expression.getExpression()).matches()) {
                transformed = expression.getMatcher().apply(params.getObjects(expression.getBinding()));
            }

            query.setParameter(expression.getBinding(), transformed);
        }
    }

    private void createPageable(Query query, DynamicQueryParams dynamicQuery) {
        DynamicQueryPageable.setQueryPageable(query, dynamicQuery);
    }

    private void addDynamicJoin(DynamicQueryParams params, FromQueryBuilder fromQueryBuilder) {
        params.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new QueryExpressionKey(key);
            Optional<QueryExpression> joinValue = dynamicQuery.getJoinValue(expressionKey);
            joinValue.ifPresent(fromQueryBuilder::join);
        });
    }

    private void addDynamicWhere(DynamicQueryParams params, WhereQueryBuilder whereQueryBuilder) {
        params.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new QueryExpressionKey(getQueryMethod().getName(), key);
            Optional<QueryExpression> filterValue = dynamicQuery.getFilterValue(expressionKey);

            filterValue.ifPresent(expression -> {
                if (expression.isFeature()) {
                    Boolean featureActive = params.getBoolean(key, false);

                    if (featureActive) {
                        whereQueryBuilder.and(expression);
                    }
                } else {
                    whereQueryBuilder.and(expression);
                }
            });
        });
    }

    private static class ValidateQuery {

        static void validate(AbstractJpaQuery jpaQuery, EntityManagerFactory factory, DynamicQueryValue dynamicQuery, Object... arguments) {
            if (jpaQuery.getQueryMethod().isProcedureQuery()) {
                return;
            }

            EntityManager validatingEm = null;
            DynamicQueryBuilder queryBuilder = DynamicQueryBuilder.newInstance();

            String selectQuery = queryBuilder
                .select(null)
                .from(dynamicQuery.getEntityClass(), dynamicQuery.getSelectQuery())
                .join()
                .where()
                .order()
                .getQuery();

            try {
                validatingEm = factory.createEntityManager();
                validatingEm.createQuery(selectQuery);
            } catch (RuntimeException e) {
                throw new IllegalArgumentException(String.format("Validation failed for query for method %s!", arguments), e);
            } finally {
                if (validatingEm != null) {
                    validatingEm.close();
                }
            }
        }

    }
}
