package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import com.github.jonpereiradev.dynamic.jpa.internal.builder.DynamicQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.internal.builder.FromQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.internal.builder.WhereQueryBuilder;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKey;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
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
    private final QueryInspector inspector;

    DynamicQueryDefJpaQuery(JpaQueryMethod jpaQueryMethod, EntityManager entityManager, DynamicQuery dynamicQuery) {
        super(jpaQueryMethod, entityManager);
        this.dynamicQuery = dynamicQuery;
        this.inspector = QueryInspectorFactory.newInspector();
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
            createPageable(query, params);
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

        params.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new QueryExpressionKeyImpl(getQueryMethod().getName(), key);
            Optional<QueryExpression> restriction = dynamicQuery.getFilterValue(expressionKey);
            restriction.ifPresent(o -> setQueryParameter(params, query, o));
        });

        return query;
    }

    private void setQueryParameter(DynamicQueryParams params, Query query, QueryExpression expression) {
        if (!expression.isFeature()) {
            Object transformed = expression.getMatcher().apply(params.getObject(expression.getBinding()));

            if (PATTERN_IN_CLAUSE.matcher(expression.getClause()).matches()) {
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
            QueryExpressionKey expressionKey = new QueryExpressionKeyImpl(key);
            Optional<QueryExpression> joinValue = dynamicQuery.getJoinValue(expressionKey);
            joinValue.ifPresent(fromQueryBuilder::join);
        });
    }

    private void addDynamicWhere(DynamicQueryParams params, WhereQueryBuilder whereQueryBuilder) {
        params.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new QueryExpressionKeyImpl(getQueryMethod().getName(), key);
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
