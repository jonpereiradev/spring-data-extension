package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKey;
import com.github.jonpereiradev.dynamic.jpa.internal.query.DynamicQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;
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
    private final DynamicQuery queryRef;

    DynamicJpaRepositoryImpl(Class<T> entityClass, EntityManager entityManager, DynamicQuery queryRef) {
        super(entityClass, entityManager);
        this.entityClass = entityClass;
        this.entityManager = entityManager;
        this.queryRef = queryRef;
    }

    @Override
    public Optional<T> findOneBy(DynamicQueryParams dynamicQuery) {
        String query = createQuery(dynamicQuery, "clazz");
        List<T> allContent = findAll(dynamicQuery, query, -1, "clazz").getContent();
        return allContent.isEmpty() ? Optional.empty() : Optional.of(allContent.get(0));
    }

    @Override
    public List<T> findAllBy(DynamicQueryParams dynamicQuery) {
        String query = createQuery(dynamicQuery, "clazz");
        return findAll(dynamicQuery, query, -1, "clazz").getContent();
    }

    @Override
    public Page<T> findAllPaged(DynamicQueryParams dynamicQuery, Pageable pageable) {
        long totalRecords = -1;

        if (pageable != null) {
            totalRecords = countBy(
                dynamicQuery,
                createCountQuery(dynamicQuery, "clazz"),
                "clazz"
            );

            if (totalRecords < 1) {
                return Page.empty();
            }
        }

        return findAll(dynamicQuery, createQuery(dynamicQuery, "clazz"), totalRecords, "clazz");
    }

    private Page<T> findAll(DynamicQueryParams dynamicQuery, String query, long totalRecords, String prefix) {
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

    private TypedQuery<T> createQueryPageableOf(String query, DynamicQueryParams dynamicQuery, String prefix) {
        TypedQuery<T> typedQuery = createQueryOf(dynamicQuery, query, entityClass, prefix);

        if (dynamicQuery.isPageable()) {
            createPageable(typedQuery, dynamicQuery);
        }

        return typedQuery;
    }

    <E> TypedQuery<E> createQueryOf(DynamicQueryParams dynamicQuery, String query, Class<E> returnType, String prefix) {
        TypedQuery<E> typedQuery = entityManager.createQuery(query, returnType);

        dynamicQuery.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new QueryExpressionKey(prefix, key);
            Optional<QueryExpression> filterValue = queryRef.getFilterValue(expressionKey);
            filterValue.ifPresent(queryExpression -> setQueryParameter(dynamicQuery, typedQuery, queryExpression));
        });

        return typedQuery;
    }

    private <E> void setQueryParameter(DynamicQueryParams dynamicQuery, TypedQuery<E> typedQuery, QueryExpression expression) {
        Object transformed;

        if (PATTERN_IN_CLAUSE.matcher(expression.getExpression()).matches()) {
            transformed = expression.getMatcher().apply(dynamicQuery.getObjects(expression.getBinding()));
        } else {
            transformed = expression.getMatcher().apply(dynamicQuery.getObject(expression.getBinding()));
        }

        typedQuery.setParameter(expression.getBinding(), transformed);
    }

    private long countBy(DynamicQueryParams dynamicQuery, String query, String prefix) {
        TypedQuery<Long> typedQuery = createQueryOf(dynamicQuery, query, Long.class, prefix);
        return getSingleResult(typedQuery).orElse(0L);
    }

    private <E> Optional<E> getSingleResult(TypedQuery<E> typedQuery) {
        List<E> resultList = typedQuery.setMaxResults(1).getResultList();
        E object = resultList.isEmpty() ? null : resultList.get(0);
        return Optional.ofNullable(object);
    }

    private void createPageable(TypedQuery<T> query, DynamicQueryParams dynamicQuery) {
        DynamicQueryPageable.setQueryPageable(query, dynamicQuery);
    }

    public String createQuery(DynamicQueryParams dynamicQuery, String prefix) {
        DynamicQueryBuilderImpl queryBuilder = new DynamicQueryBuilderImpl(queryRef);
        return queryBuilder.select().join(dynamicQuery).where(dynamicQuery, prefix).sorted(dynamicQuery).query();
    }

    public String createCountQuery(DynamicQueryParams dynamicQuery, String prefix) {
        DynamicQueryBuilderImpl queryBuilder = new DynamicQueryBuilderImpl(queryRef);
        return queryBuilder.count().join(dynamicQuery).where(dynamicQuery, prefix).query();
    }

}
