package com.github.jonpereiradev.dynamic.jpa;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.provider.PersistenceProvider;
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
    private final EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

    DynamicJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        this.provider = PersistenceProvider.fromEntityManager(entityManager);
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider queryProvider) {
        QueryLookupStrategy strategy = DynamicQueryLookupStrategy.create(entityManager, key, provider, queryProvider, escapeCharacter);
        return Optional.of(strategy);
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (isDynamicJpaRepository(metadata.getRepositoryInterface())) {
            return DynamicJpaRepositoryImpl.class;
        }

        return SimpleJpaRepository.class;
    }

    @Override
    protected JpaRepositoryImplementation<T, ID> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        if (isDynamicJpaRepository(information.getRepositoryInterface())) {
            logger.info("Configuring Dynamic Jpa Repository for " + information.getRepositoryInterface().getName());
            return createDynamicJpaRepository(information, entityManager);
        }

        return createJpaRepository(information, entityManager);
    }

    private boolean isDynamicJpaRepository(Class<?> repositoryInterface) {
        for (Class<?> anInterface : repositoryInterface.getInterfaces()) {
            if (anInterface.equals(DynamicJpaRepository.class)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private JpaRepositoryImplementation<T, ID> createJpaRepository(RepositoryInformation information, EntityManager entityManager) {
        return new SimpleJpaRepository<>((Class<T>) information.getDomainType(), entityManager);
    }

    @SuppressWarnings("unchecked")
    private JpaRepositoryImplementation<T, ID> createDynamicJpaRepository(RepositoryInformation information, EntityManager entityManager) {
        DynamicQueryValueFactory dynamicQueryFactory = new DynamicQueryValueFactory(information);
        DynamicQueryValue dynamicQueryValue = dynamicQueryFactory.create();

        return new DynamicJpaRepositoryImpl<>((Class<T>) information.getDomainType(), entityManager, dynamicQueryValue);
    }

}


