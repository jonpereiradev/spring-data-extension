package com.github.jonpereiradev.dynamic.jpa;


import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaParametersParameterAccessor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.repository.core.RepositoryMetadata;

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

    private final DynamicQueryValue queryValue;

    DynamicQueryDefJpaQuery(JpaQueryMethod jpaQueryMethod, EntityManager entityManager, DynamicQueryValue queryValue) {
        super(jpaQueryMethod, entityManager);
        this.queryValue = queryValue;
        ValidateQuery.validate(this, getEntityManager().getEntityManagerFactory(), queryValue.getQuery(), jpaQueryMethod);
    }

    @Override
    protected Query doCreateQuery(JpaParametersParameterAccessor accessor) {
        DynamicQueryBuilder queryBuilder = new DynamicQueryBuilder(queryValue);
        DynamicQueryParams queryParams = (DynamicQueryParams) accessor.getValues()[0];
        String queryString = queryBuilder.select().join(queryParams).where(queryParams, getQueryMethod()).sorted(queryParams).query();

        Query query = createQueryOf(queryParams, queryString);

        if (queryParams.isPageable()) {
            createPageable(query, queryParams);
        }

        return query;
    }

    @Override
    protected Query doCreateCountQuery(JpaParametersParameterAccessor accessor) {
        DynamicQueryParams dynamicQuery = (DynamicQueryParams) accessor.getValues()[0];
        DynamicQueryBuilder queryBuilder = new DynamicQueryBuilder(queryValue);
        String countQuery = queryBuilder.count().join(dynamicQuery).where(dynamicQuery, getQueryMethod().getName()).sorted(dynamicQuery).query();
        return createQueryOf(dynamicQuery, countQuery);
    }

    private Query createQueryOf(DynamicQueryParams dynamicQuery, String queryString) {
        Query query = getEntityManager().createQuery(queryString);

        dynamicQuery.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new QueryExpressionKey(getQueryMethod().getName(), key);
            Optional<QueryExpression> restriction = queryValue.getFilterValue(expressionKey);
            restriction.ifPresent(o -> setQueryParameter(dynamicQuery, query, o));
        });

        return query;
    }

    private void setQueryParameter(DynamicQueryParams dynamicQuery, Query query, QueryExpression o) {
        Object transformed = o.getMatcher().apply(dynamicQuery.getObject(o.getBinding()));

        if (PATTERN_IN_CLAUSE.matcher(o.getExpression()).matches()) {
            transformed = o.getMatcher().apply(dynamicQuery.getObjects(o.getBinding()));
        }

        query.setParameter(o.getBinding(), transformed);
    }

    private void createPageable(Query query, DynamicQueryParams dynamicQuery) {
        DynamicQueryPageable.setQueryPageable(query, dynamicQuery);
    }


    private static class ValidateQuery {

        static void validate(AbstractJpaQuery jpaQuery, EntityManagerFactory factory, String query, Object... arguments) {
            if (jpaQuery.getQueryMethod().isProcedureQuery()) {
                return;
            }

            EntityManager validatingEm = null;

            try {
                validatingEm = factory.createEntityManager();
                validatingEm.createQuery(query);
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
