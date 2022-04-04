package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.query.DynamicQueryDef;
import com.github.jonpereiradev.dynamic.jpa.query.DynamicQueryDefFactory;
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


public class DynamicJpaRepositoryFactory<T, ID extends Serializable> extends JpaRepositoryFactory {

    private final EntityManager entityManager;
    private final PersistenceProvider provider;
    private final EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;

    DynamicJpaRepositoryFactory(EntityManager entityManager) {
        super(entityManager);
        this.entityManager = entityManager;
        this.provider = PersistenceProvider.fromEntityManager(entityManager);
    }

    @Override
    protected JpaRepositoryImplementation<T, ID> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
        if (isDynamicJpaRepository(information.getRepositoryInterface())) {
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
    private SimpleJpaRepository<T, ID> createJpaRepository(RepositoryInformation information, EntityManager entityManager) {
        return new SimpleJpaRepository<>((Class<T>) information.getDomainType(), entityManager);
    }

    @SuppressWarnings("unchecked")
    private DynamicJpaRepositoryImpl<T, ID> createDynamicJpaRepository(RepositoryInformation information, EntityManager entityManager) {
        DynamicQueryDefFactory dynamicQueryFactory = new DynamicQueryDefFactory(information);
        DynamicQueryDef dynamicQueryDef = dynamicQueryFactory.createRef();

        return new DynamicJpaRepositoryImpl<>((Class<T>) information.getDomainType(), entityManager, dynamicQueryDef);
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(
        QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider
    ) {
        return Optional.of(DynamicQueryLookupStrategy.create(
            entityManager,
            key,
            provider,
            evaluationContextProvider,
            escapeCharacter
        ));
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        if (isDynamicJpaRepository(metadata.getRepositoryInterface())) {
            return DynamicJpaRepositoryImpl.class;
        }

        return SimpleJpaRepository.class;
    }

}


