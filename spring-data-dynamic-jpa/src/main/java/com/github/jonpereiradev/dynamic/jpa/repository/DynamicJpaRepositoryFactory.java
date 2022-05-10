package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import com.github.jonpereiradev.dynamic.jpa.internal.query.DynamicQuery;
import com.github.jonpereiradev.dynamic.jpa.internal.query.DynamicQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Responsible for configure and create the {@link DynamicJpaRepositoryImpl} instance.
 *
 * @param <T>
 * @param <ID>
 */
final class DynamicJpaRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {

    private final Logger logger = LoggerFactory.getLogger(DynamicJpaRepositoryFactory.class);

    private final EntityManager entityManager;
    private final PersistenceProvider provider;
    private final EscapeCharacter escapeCharacter;
    private final DynamicJpaRepositoryValidator validator;

    DynamicJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        this.escapeCharacter = EscapeCharacter.DEFAULT;
        this.provider = PersistenceProvider.fromEntityManager(entityManager);
        this.validator = new DynamicJpaRepositoryValidator();
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider queryProvider) {
        QueryLookupStrategy strategy = DynamicQueryLookupStrategy.create(entityManager, key, provider, queryProvider, escapeCharacter);
        return Optional.of(strategy);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (validator.isDynamicRepository(metadata.getRepositoryInterface())) {
            return DynamicJpaRepositoryImpl.class;
        }

        return SimpleJpaRepository.class;
    }

    @Override
    protected JpaRepositoryImplementation<T, ID> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        if (validator.isDynamicRepository(information.getRepositoryInterface())) {
            logger.info("Configuring repository {}", information.getRepositoryInterface().getName());
            return createDynamicJpaRepository(information, entityManager);
        }

        return createJpaRepository(information, entityManager);
    }

    @SuppressWarnings("unchecked")
    private JpaRepositoryImplementation<T, ID> createJpaRepository(RepositoryInformation information, EntityManager entityManager) {
        return new SimpleJpaRepository<>((Class<T>) information.getDomainType(), entityManager);
    }

    @SuppressWarnings("unchecked")
    private JpaRepositoryImplementation<T, ID> createDynamicJpaRepository(RepositoryInformation information, EntityManager entityManager) {
        DynamicQueryFactory factory = DynamicQueryFactory.newInstance(information);
        DynamicQuery findOneByDynamicQuery = createDynamicQuery(information, factory, "findOneBy", DynamicQueryParams.class);
        DynamicQuery findAllByDynamicQuery = createDynamicQuery(information, factory, "findAllBy", DynamicQueryParams.class);
        DynamicQuery findAllPagedDynamicQuery = createDynamicQuery(information, factory, "findAllPaged", DynamicQueryParams.class, Pageable.class);

        return new DynamicJpaRepositoryImpl<>(
            entityManager,
            (Class<T>) information.getDomainType(),
            findOneByDynamicQuery,
            findAllByDynamicQuery,
            findAllPagedDynamicQuery
        );
    }

    private DynamicQuery createDynamicQuery(RepositoryInformation information, DynamicQueryFactory factory, String methodName, Class<?>... parameters) {
        DynamicQuery dynamicQuery;

        try {
            Method method = information.getRepositoryInterface().getDeclaredMethod(methodName, parameters);

            if (method.isAnnotationPresent(Query.class)) {
                dynamicQuery = null;
            } else {
                dynamicQuery = factory.newInstance(method);
            }
        } catch (NoSuchMethodException e) {
            try {
                Method method = DynamicJpaRepository.class.getDeclaredMethod(methodName, parameters);
                dynamicQuery = factory.newInstance(method);
            } catch (NoSuchMethodException e2) {
                throw new UnsupportedOperationException(e2);
            }
        }

        return dynamicQuery;
    }

}


