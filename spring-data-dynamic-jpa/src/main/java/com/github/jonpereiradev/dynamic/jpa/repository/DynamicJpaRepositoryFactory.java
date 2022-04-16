package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.internal.query.DynamicQuery;
import com.github.jonpereiradev.dynamic.jpa.internal.query.DynamicQueryFactory;
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
        DynamicQueryFactory dynamicQueryFactory = DynamicQueryFactory.newInstance(information);
        DynamicQuery dynamicQuery = dynamicQueryFactory.newInstance();

        return new DynamicJpaRepositoryImpl<>((Class<T>) information.getDomainType(), entityManager, dynamicQuery);
    }

}


