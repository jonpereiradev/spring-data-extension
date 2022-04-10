package com.github.jonpereiradev.dynamic.jpa;


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
        private final DynamicJpaRepositoryValidator validator;

        private QueryDefLookupStrategy(QueryLookupStrategy queryLookupStrategy, EntityManager entityManager, QueryExtractor extractor) {
            this.queryLookupStrategy = queryLookupStrategy;
            this.entityManager = entityManager;
            this.extractor = extractor;
            this.validator = new DynamicJpaRepositoryValidator();
        }

        @Override
        public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory, NamedQueries queries) {
            if (validator.isDynamicRepository(metadata.getRepositoryInterface()) && validator.isDynamicMethod(method)) {
                return createJpaRepositoryQuery(method, metadata, factory);
            }

            return queryLookupStrategy.resolveQuery(method, metadata, factory, queries);
        }

        private RepositoryQuery createJpaRepositoryQuery(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
            DynamicQueryValueFactory dynamicQueryValueFactory = new DynamicQueryValueFactory(metadata);
            DynamicQueryValue dynamicQueryValue = dynamicQueryValueFactory.newDynamicQueryValue(method);
            JpaQueryMethod jpaQueryMethod = getJpaQueryMethod(method, metadata, factory);

            return new DynamicQueryDefJpaQuery(jpaQueryMethod, entityManager, dynamicQueryValue);
        }

        private JpaQueryMethod getJpaQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
            return new DefaultJpaQueryMethodFactory(extractor).build(method, metadata, factory);
        }

    }
}
