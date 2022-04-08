package com.github.jonpereiradev.dynamic.jpa;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.query.DefaultJpaQueryMethodFactory;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;


/**
 * {@see org.springframework.data.jpa.repository.query.JpaQueryLookupStrategy}
 */
final class DynamicQueryLookupStrategy {

    private DynamicQueryLookupStrategy() {
    }

    static QueryLookupStrategy create(
        EntityManager entityManager,
        Key key,
        QueryExtractor extractor,
        QueryMethodEvaluationContextProvider provider,
        EscapeCharacter escape
    ) {
        DefaultJpaQueryMethodFactory queryMethodFactory = new DefaultJpaQueryMethodFactory(extractor);
        QueryLookupStrategy queryLookupStrategy = JpaQueryLookupStrategy.create(entityManager, queryMethodFactory, key, provider, escape);

        return new QueryDefLookupStrategy(queryLookupStrategy, entityManager, extractor);
    }

    private static class QueryDefLookupStrategy implements QueryLookupStrategy {

        private final QueryLookupStrategy queryLookupStrategy;
        private final EntityManager entityManager;
        private final QueryExtractor extractor;

        private QueryDefLookupStrategy(QueryLookupStrategy queryLookupStrategy, EntityManager entityManager, QueryExtractor extractor) {
            this.queryLookupStrategy = queryLookupStrategy;
            this.entityManager = entityManager;
            this.extractor = extractor;
        }

        @Override
        public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries queries) {
            if (isDynamicQueryMethod(method)) {
                return createJpaRepositoryQuery(method, metadata, factory);
            }

            return queryLookupStrategy.resolveQuery(method, metadata, factory, queries);
        }

        private RepositoryQuery createJpaRepositoryQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
            validateQueryMethod(method);

            DynamicQueryValueFactory configurationFactory = new DynamicQueryValueFactory(metadata);
            JpaQueryMethod jpaQueryMethod = getJpaQueryMethod(method, metadata, factory);
            DynamicQueryValue queryValue = configurationFactory.create(method);

            return new DynamicQueryDefJpaQuery(jpaQueryMethod, entityManager, queryValue);
        }

        private JpaQueryMethod getJpaQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
            return new DefaultJpaQueryMethodFactory(extractor).build(method, metadata, factory);
        }

        private void validateQueryMethod(Method method) {
            Class<?>[] parameters = method.getParameterTypes();

            if (parameters.length == 0 || parameters.length > 2
                || isInvalidParameterClass(parameters, DynamicQueryParams.class)
                || isInvalidParameterClass(parameters, DynamicQueryParams.class, Pageable.class)
            ) {
                throw new IllegalArgumentException("Method " + method + " must have only one parameter of type DynamicQuery");
            }
        }

        private boolean isInvalidParameterClass(Class<?>[] classes, Class<?>... validClasses) {
            if (validClasses.length != classes.length) {
                return false;
            }

            boolean isAnyClass = true;

            for (int i = 0; i < classes.length; i++) {
                if (!classes[i].equals(validClasses[i])) {
                    isAnyClass = false;
                    break;
                }
            }

            return !isAnyClass;
        }

        private boolean isDynamicQueryMethod(Method method) {
            Class<?>[] parameters = method.getParameterTypes();

            if (parameters.length == 1 && parameters[0].equals(DynamicQueryParams.class)) {
                return true;
            }

            if (parameters.length == 2) {
                for (Class<?> parameter : parameters) {
                    if (!parameter.isAssignableFrom(DynamicQueryParams.class) || !parameter.isAssignableFrom(Pageable.class)) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }
    }

}
