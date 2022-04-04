package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.query.DynamicQuery;
import com.github.jonpereiradev.dynamic.jpa.query.DynamicQueryDefFactory;
import com.github.jonpereiradev.dynamic.jpa.query.DynamicSelect;
import com.github.jonpereiradev.dynamic.jpa.query.NativeQueryDef;
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
public final class DynamicQueryLookupStrategy {

    private DynamicQueryLookupStrategy() {
    }

    public static QueryLookupStrategy create(
        EntityManager entityManager,
        Key key,
        QueryExtractor extractor,
        QueryMethodEvaluationContextProvider provider,
        EscapeCharacter escape
    ) {
        QueryLookupStrategy queryLookupStrategy = JpaQueryLookupStrategy.create(
            entityManager,
            new DefaultJpaQueryMethodFactory(extractor),
            key,
            provider,
            escape
        );

        return new QueryDefLookupStrategy(queryLookupStrategy, entityManager, extractor);
    }

    private static class QueryDefLookupStrategy implements QueryLookupStrategy {

        private final QueryLookupStrategy queryLookupStrategy;
        private final EntityManager entityManager;
        private final QueryExtractor extractor;

        private QueryDefLookupStrategy(
            QueryLookupStrategy queryLookupStrategy,
            EntityManager entityManager,
            QueryExtractor extractor
        ) {
            this.queryLookupStrategy = queryLookupStrategy;
            this.entityManager = entityManager;
            this.extractor = extractor;
        }

        @Override
        public RepositoryQuery resolveQuery(
            Method method,
            RepositoryMetadata metadata,
            ProjectionFactory factory,
            NamedQueries queries
        ) {
            if (isDynamicQueryMethod(method)) {
                return createJpaRepositoryQuery(method, metadata, factory);
            } else if (method.isAnnotationPresent(NativeQueryDef.class)) {
                return createNativeRepositoryQuery(method, metadata, factory);
            } else {
                return queryLookupStrategy.resolveQuery(method, metadata, factory, queries);
            }
        }

        private boolean isDynamicQueryMethod(Method method) {
            Class<?>[] parameters = method.getParameterTypes();

            if (parameters.length == 1 && parameters[0].equals(DynamicQuery.class)) {
                return true;
            }

            if (parameters.length == 2) {
                for (Class<?> parameter : parameters) {
                    if (!parameter.isAssignableFrom(DynamicQuery.class) || !parameter.isAssignableFrom(Pageable.class)) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }

        private RepositoryQuery createJpaRepositoryQuery(
            Method method,
            RepositoryMetadata metadata,
            ProjectionFactory factory
        ) {
            validateQueryMethod(method);

            DynamicQueryDefFactory configurationFactory = new DynamicQueryDefFactory(metadata);

            return new DynamicQueryDefJpaQuery(
                getJpaQueryMethod(method, metadata, factory),
                entityManager,
                configurationFactory.createRef()
            );
        }

        private JpaQueryMethod getJpaQueryMethod(
            Method method,
            RepositoryMetadata metadata,
            ProjectionFactory factory
        ) {
            return new DefaultJpaQueryMethodFactory(extractor).build(method, metadata, factory);
        }

        private RepositoryQuery createNativeRepositoryQuery(
            Method method,
            RepositoryMetadata metadata,
            ProjectionFactory factory
        ) {
            NativeQueryDef nativeQueryDef = method.getAnnotation(NativeQueryDef.class);

            return new NativeQueryDefJpaQuery(
                nativeQueryDef,
                getJpaQueryMethod(method, metadata, factory),
                entityManager
            );
        }

        private void validateQueryMethod(Method method) {
            Class<?>[] parameters = method.getParameterTypes();

            if (parameters.length == 0
                || parameters.length > 2
                || isInvalidParameterClass(parameters, DynamicQuery.class)
                || isInvalidParameterClass(parameters, DynamicQuery.class, Pageable.class)
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

    }

}
