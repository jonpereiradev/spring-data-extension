package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryPageable;
import com.github.jonpereiradev.dynamic.jpa.Restriction;
import com.github.jonpereiradev.dynamic.jpa.query.DynamicQuery;
import com.github.jonpereiradev.dynamic.jpa.query.DynamicQueryDef;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaParametersParameterAccessor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Optional;
import java.util.regex.Pattern;


/**
 * Responsible to handle queries for methods declared on the interface created by the developer.
 */
final class DynamicQueryDefJpaQuery extends AbstractJpaQuery {

    private static final Pattern PATTERN_IN_CLAUSE = Pattern.compile(".*\\sin\\s?\\(.*");

    private final DynamicQueryDef queryDef;
    private final FilterDefNameResolver resolver;

    DynamicQueryDefJpaQuery(JpaQueryMethod jpaQueryMethod, EntityManager entityManager, DynamicQueryDef queryDef) {
        super(jpaQueryMethod, entityManager);
        this.queryDef = queryDef;
        this.resolver = new FilterDefNameResolver(queryDef.getFilterDef());
        validateQuery(queryDef.getQuery(), jpaQueryMethod);
    }

    private void validateQuery(String query, Object... arguments) {
        if (getQueryMethod().isProcedureQuery()) {
            return;
        }

        EntityManager validatingEm = null;

        try {
            validatingEm = getEntityManager().getEntityManagerFactory().createEntityManager();
            validatingEm.createQuery(query);

        } catch (RuntimeException e) {
            throw new IllegalArgumentException(
                String.format("Validation failed for query for method %s!", arguments),
                e
            );
        } finally {
            if (validatingEm != null) {
                validatingEm.close();
            }
        }
    }

    @Override
    protected Query doCreateQuery(JpaParametersParameterAccessor accessor) {
        DynamicQuery dynamicQuery = (DynamicQuery) accessor.getValues()[0];
        DynamicQueryBuilder queryBuilder = new DynamicQueryBuilder(queryDef);

        String queryString = queryBuilder
            .select()
            .join(dynamicQuery)
            .where(dynamicQuery, getQueryMethod())
            .sorted(dynamicQuery)
            .query();

        Query query = createQueryOf(dynamicQuery, queryString);

        if (dynamicQuery.isPageable()) {
            createPageable(query, dynamicQuery);
        }

        return query;
    }

    @Override
    protected Query doCreateCountQuery(JpaParametersParameterAccessor accessor) {
        DynamicQuery dynamicQuery = (DynamicQuery) accessor.getValues()[0];
        DynamicQueryBuilder queryBuilder = new DynamicQueryBuilder(queryDef);
        String countQuery = queryBuilder.count().join(dynamicQuery).where(dynamicQuery, getQueryMethod().getName()).sorted(dynamicQuery).query();
        return createQueryOf(dynamicQuery, countQuery);
    }

    private Query createQueryOf(DynamicQuery dynamicQuery, String queryString) {
        Query query = getEntityManager().createQuery(queryString);

        dynamicQuery.getParameters().forEach((key, value) -> {
            String filterName = resolver.resolve(key, getQueryMethod().getName());
            Optional<Restriction> restriction = queryDef.getFilterDef().get(filterName);
            restriction.ifPresent(o -> setQueryParameter(dynamicQuery, query, o));
        });

        return query;
    }

    private void setQueryParameter(DynamicQuery dynamicQuery, Query query, Restriction o) {
        Object transformed = o.getMatcher().apply(dynamicQuery.getObject(o.getName()));

        if (PATTERN_IN_CLAUSE.matcher(o.getClause()).matches()) {
            transformed = o.getMatcher().apply(dynamicQuery.getObjects(o.getName()));
        }

        query.setParameter(o.getName(), transformed);
    }

    private void createPageable(Query query, DynamicQuery dynamicQuery) {
        DynamicQueryPageable.setQueryPageable(query, dynamicQuery);
    }

}
