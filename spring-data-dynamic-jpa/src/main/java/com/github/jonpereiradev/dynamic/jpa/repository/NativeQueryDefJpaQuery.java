package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.NativeQuery;
import com.github.jonpereiradev.dynamic.jpa.query.NativeQueryDef;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaParametersParameterAccessor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;

import javax.persistence.EntityManager;
import javax.persistence.Query;


final class NativeQueryDefJpaQuery extends AbstractJpaQuery {

    private static final String SELECT_COUNT = "select count(1) ";

    private final NativeQueryDef nativeQueryDef;
    private final NativeQuery nativeQuery;

    NativeQueryDefJpaQuery(NativeQueryDef nativeQueryDef, JpaQueryMethod jpaQueryMethod, EntityManager entityManager) {
        super(jpaQueryMethod, entityManager);
        this.nativeQueryDef = nativeQueryDef;
        this.nativeQuery = NativeQuery.defaultInstance();
    }

    @Override
    protected Query doCreateQuery(JpaParametersParameterAccessor accessor) {
        Object[] parameters = accessor.getValues();
        String queryString = nativeQuery.readQuery(nativeQueryDef.value()).toString();
        Query query = getEntityManager().createNativeQuery(queryString);
        setSelectParameters(parameters, query);
        return query;
    }

    private void setSelectParameters(Object[] parameters, Query query) {
        int position = 0;

        for (Object parameter : parameters) {
            if (parameter instanceof Pageable) {
                setPageableParameter(query, (Pageable) parameter);
            } else {
                query.setParameter(++position, parameter);
            }
        }
    }

    private void setPageableParameter(Query query, Pageable pageable) {
        if (pageable.isPaged()) {
            query.setMaxResults(pageable.getPageSize());
            query.setFirstResult(Long.valueOf(pageable.getOffset()).intValue());
        }
    }

    @Override
    protected Query doCreateCountQuery(JpaParametersParameterAccessor accessor) {
        Object[] parameters = accessor.getValues();
        String queryString = nativeQuery.readQuery(nativeQueryDef.value()).toString();
        queryString = SELECT_COUNT + queryString.substring(queryString.indexOf("from"));
        Query query = getEntityManager().createNativeQuery(queryString);
        setSelectCountParameters(parameters, query);
        return query;
    }

    private void setSelectCountParameters(Object[] parameters, Query query) {
        int position = 0;

        for (Object parameter : parameters) {
            if (!(parameter instanceof Pageable)) {
                query.setParameter(++position, parameter);
            }
        }
    }

}
