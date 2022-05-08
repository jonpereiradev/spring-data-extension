package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import com.github.jonpereiradev.dynamic.jpa.internal.builder.DynamicQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.internal.builder.FromQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.internal.builder.WhereQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.FilterExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.JoinExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKey;
import com.github.jonpereiradev.dynamic.jpa.internal.query.DynamicQuery;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaParametersParameterAccessor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;


/**
 * Responsible to handle queries for methods declared on the interface created by the developer.
 */
final class DynamicQueryDefJpaQuery extends AbstractJpaQuery {

    private static final Pattern PATTERN_IN_CLAUSE = Pattern.compile(".*\\sin\\s?\\(.*");

    private final DynamicQuery dynamicQuery;

    DynamicQueryDefJpaQuery(JpaQueryMethod jpaQueryMethod, EntityManager entityManager, DynamicQuery dynamicQuery) {
        super(jpaQueryMethod, entityManager);
        this.dynamicQuery = dynamicQuery;
        new ValidateQuery().validate(dynamicQuery, jpaQueryMethod);
    }

    @Override
    protected Query doCreateQuery(JpaParametersParameterAccessor accessor) {
        DynamicQueryBuilder builder = DynamicQueryBuilder.newInstance(dynamicQuery.getEntityClass());
        DynamicQueryParams params = (DynamicQueryParams) accessor.getValues()[0];
        FromQueryBuilder fromQueryBuilder = builder.select(dynamicQuery.getSelectQuery()).from().join();

        addDynamicJoin(params, fromQueryBuilder);

        WhereQueryBuilder whereQueryBuilder = fromQueryBuilder.where();
        addDynamicWhere(params, whereQueryBuilder);

        String selectQuery = whereQueryBuilder.order().toString();
        Query query = createQueryOf(params, selectQuery);

        if (params.isPageable()) {
            DynamicQueryPageable.setQueryPageable(query, params);
        }

        return query;
    }

    @Override
    protected Query doCreateCountQuery(JpaParametersParameterAccessor accessor) {
        DynamicQueryBuilder builder = DynamicQueryBuilder.newInstance(dynamicQuery.getEntityClass());
        DynamicQueryParams params = (DynamicQueryParams) accessor.getValues()[0];
        FromQueryBuilder fromQueryBuilder = builder.count(dynamicQuery.getSelectQuery()).from().join();

        addDynamicJoin(params, fromQueryBuilder);

        WhereQueryBuilder whereQueryBuilder = fromQueryBuilder.where();
        addDynamicWhere(params, whereQueryBuilder);

        String countQuery = whereQueryBuilder.order().toString();
        return createQueryOf(params, countQuery);
    }

    private Query createQueryOf(DynamicQueryParams params, String queryString) {
        Query query = getEntityManager().createQuery(queryString);

        params.getParameters().forEach((key, ignored) -> {
            QueryExpressionKey expressionKey = new FilterExpressionKeyImpl(getQueryMethod().getName(), key);
            Optional<QueryExpression> expression = dynamicQuery.getExpression(expressionKey);
            expression.ifPresent(value -> setQueryParameter(params, query, value));
        });

        return query;
    }

    private void setQueryParameter(DynamicQueryParams params, Query query, QueryExpression expression) {
        if (!expression.isFeature()) {
            Object transformed;

            if (PATTERN_IN_CLAUSE.matcher(expression.getClause()).matches()) {
                String[] parameters = params.getStringArray(expression.getBinding());
                transformed = expression.getConverter().convertValueArray(parameters);
            } else {
                String parameter = params.getString(expression.getBinding());
                transformed = expression.getConverter().convertValue(parameter);
            }

            query.setParameter(expression.getBinding(), transformed);
        }
    }

    private void addDynamicJoin(DynamicQueryParams params, FromQueryBuilder fromQueryBuilder) {
        params.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new JoinExpressionKeyImpl(key);
            Optional<QueryExpression> joinValue = dynamicQuery.getExpression(expressionKey);
            joinValue.ifPresent(fromQueryBuilder::join);
        });
    }

    private void addDynamicWhere(DynamicQueryParams params, WhereQueryBuilder whereQueryBuilder) {
        params.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new FilterExpressionKeyImpl(getQueryMethod().getName(), key);
            Optional<QueryExpression> filterValue = dynamicQuery.getExpression(expressionKey);

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

    private class ValidateQuery {

        void validate(DynamicQuery dynamicQuery, Object... arguments) {
            if (getQueryMethod().isProcedureQuery()) {
                return;
            }

            EntityManager validatingEm = null;
            DynamicQueryBuilder builder = DynamicQueryBuilder.newInstance(dynamicQuery.getEntityClass());
            String selectQuery = builder.select(dynamicQuery.getSelectQuery()).from().join().where().order().toString();

            try {
                validatingEm = getEntityManager().getEntityManagerFactory().createEntityManager();
                validatingEm.createQuery(selectQuery);
            } catch (RuntimeException e) {
                throw new IllegalArgumentException(String.format("Validation failed for query for method %s!", Arrays.toString(arguments)), e);
            } finally {
                if (validatingEm != null) {
                    validatingEm.close();
                }
            }
        }

    }
}
