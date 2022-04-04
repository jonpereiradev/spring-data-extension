package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryPageable;
import com.github.jonpereiradev.dynamic.jpa.RecursiveParameter;
import com.github.jonpereiradev.dynamic.jpa.Restriction;
import com.github.jonpereiradev.dynamic.jpa.query.DynamicQuery;
import com.github.jonpereiradev.dynamic.jpa.query.DynamicQueryDef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;


/**
 * Responsible for the default methods provided by the {@link DynamicJpaRepository}.
 *
 * @param <T>
 * @param <ID>
 */
@Repository
final class DynamicJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements DynamicJpaRepository<T, ID> {

    private static final Pattern PATTERN_IN_CLAUSE = Pattern.compile(".*\\sin\\s?\\(?(:\\w+)\\)?", Pattern.CASE_INSENSITIVE);

    private final Class<T> entityClass;
    private final EntityManager entityManager;
    private final DynamicQueryDef queryRef;
    private final FilterDefNameResolver resolver;

    DynamicJpaRepositoryImpl(Class<T> entityClass, EntityManager entityManager, DynamicQueryDef queryRef) {
        super(entityClass, entityManager);
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.queryRef = queryRef;
        this.resolver = new FilterDefNameResolver(queryRef.getFilterDef());
    }

    @Override
    public Optional<T> findOneBy(DynamicQuery dynamicQuery) {
        String query = createQuery(dynamicQuery, "findOneBy");
        List<T> allContent = findAll(dynamicQuery, query, -1, "findOneBy").getContent();
        return allContent.isEmpty() ? Optional.empty() : Optional.of(allContent.get(0));
    }

    @Override
    public List<T> findAllBy(DynamicQuery dynamicQuery) {
        String query = createQuery(dynamicQuery, "findAllBy");
        return findAll(dynamicQuery, query, -1, "findAllBy").getContent();
    }

    @Override
    public Page<T> findAllPaged(DynamicQuery dynamicQuery, Pageable pageable) {
        long totalRecords = -1;

        if (pageable != null) {
            totalRecords = countBy(
                dynamicQuery,
                createCountQuery(dynamicQuery, "findAllPaged"),
                "findAllPaged"
            );

            if (totalRecords < 1) {
                return Page.empty();
            }
        }

        return findAll(dynamicQuery, createQuery(dynamicQuery, "findAllPaged"), totalRecords, "findAllPaged");
    }

    @Override
    public Optional<T> findEquals(T entity) {
        return findEquals(entity, RecursiveParameter.newInstance(entity));
    }

    @Override
    public Optional<T> findEquals(T entity, RecursiveParameter<T> recursiveParameter) {
//        DynamicQueryBuilder queryBuilder = new DynamicQueryBuilder(queryRef.getQuery())
//            .append("where 1 = 1")
//            .append(recursiveParameter.generate());
//
//        TypedQuery<T> typeQuery = entityManager.createQuery(queryBuilder.toString(), entityClass);
//        recursiveParameter.setQueryParameters(typeQuery);
//
//        return getSingleResult(typeQuery);
        return Optional.empty();
    }

    @Override
    public <R> R nativeQuery(DynamicQueryBuilder queryBuilder, Function<R, R> consumer) {
        String queryString = queryBuilder.toString();
        Query query = entityManager.createNativeQuery(queryString);

        // TODO IMPLEMENTAR CONSTRUÇÃO DE QUERY NATIVA DE ARQUIVOS

        return null;
    }

    @Override
    public void refresh(T entity) {
        entityManager.refresh(entity);
    }

    private Page<T> findAll(DynamicQuery dynamicQuery, String query, long totalRecords, String prefix) {
        TypedQuery<T> typedQuery = createQueryPageableOf(query, dynamicQuery, prefix);
        List<T> resultList = typedQuery.getResultList();

        if (totalRecords == -1) {
            totalRecords = resultList.size();
        }

        if (dynamicQuery.isPageable()) {
            Pageable pageable = dynamicQuery.getPageable();
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, pageable.getSort());
            return totalRecords > 0 ? new PageImpl<>(resultList, pageRequest, totalRecords) : Page.empty(pageRequest);
        }

        if (totalRecords == 0) {
            return Page.empty();
        }

        PageRequest pageRequest = PageRequest.of(0, (int) totalRecords, dynamicQuery.getSort());
        return totalRecords > 0 ? new PageImpl<>(resultList, pageRequest, totalRecords) : Page.empty(pageRequest);
    }

    private TypedQuery<T> createQueryPageableOf(String query, DynamicQuery dynamicQuery, String prefix) {
        TypedQuery<T> typedQuery = createQueryOf(dynamicQuery, query, entityClass, prefix);

        if (dynamicQuery.isPageable()) {
            createPageable(typedQuery, dynamicQuery);
        }

        return typedQuery;
    }

    <E> TypedQuery<E> createQueryOf(DynamicQuery dynamicQuery, String query, Class<E> returnType, String prefix) {
        TypedQuery<E> typedQuery = entityManager.createQuery(query, returnType);

        dynamicQuery.getParameters().forEach((key, value) -> {
            String keyName = resolver.resolve(key, prefix);
            Optional<Restriction> restriction = queryRef.getFilterDef().get(keyName);
            restriction.ifPresent(o -> setQueryParameter(dynamicQuery, typedQuery, o));
        });

        return typedQuery;
    }

    private <E> void setQueryParameter(DynamicQuery dynamicQuery, TypedQuery<E> typedQuery, Restriction restriction) {
        Object transformed;

        if (PATTERN_IN_CLAUSE.matcher(restriction.getClause()).matches()) {
            transformed = restriction.getMatcher().apply(dynamicQuery.getObjects(restriction.getName()));
        } else {
            transformed = restriction.getMatcher().apply(dynamicQuery.getObject(restriction.getName()));
        }

        typedQuery.setParameter(restriction.getName(), transformed);
    }

    private long countBy(DynamicQuery dynamicQuery, String query, String prefix) {
        TypedQuery<Long> typedQuery = createQueryOf(dynamicQuery, query, Long.class, prefix);
        return getSingleResult(typedQuery).orElse(0L);
    }

    private <E> Optional<E> getSingleResult(TypedQuery<E> typedQuery) {
        List<E> resultList = typedQuery.setMaxResults(1).getResultList();
        E object = resultList.isEmpty() ? null : resultList.get(0);
        return Optional.ofNullable(object);
    }

    private void createPageable(TypedQuery<T> query, DynamicQuery dynamicQuery) {
        DynamicQueryPageable.setQueryPageable(query, dynamicQuery);
    }

    public String createQuery(DynamicQuery dynamicQuery, String prefix) {
        DynamicQueryBuilder queryBuilder = new DynamicQueryBuilder(queryRef);
        return queryBuilder.select().join(dynamicQuery).where(dynamicQuery, prefix).sorted(dynamicQuery).query();
    }

    public String createCountQuery(DynamicQuery dynamicQuery, String prefix) {
        DynamicQueryBuilder queryBuilder = new DynamicQueryBuilder(queryRef);
        return queryBuilder.count().join(dynamicQuery).where(dynamicQuery, prefix).query();
    }

}
