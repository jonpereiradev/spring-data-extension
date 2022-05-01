package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.FilterExpressionKeyImpl;
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
    private final DynamicQuery findOneByDynamicQuery;
    private final DynamicQuery findAllByDynamicQuery;
    private final DynamicQuery findAllPagedDynamicQuery;

    DynamicJpaRepositoryImpl(
        EntityManager entityManager,
        Class<T> entityClass,
        DynamicQuery findOneByDynamicQuery,
        DynamicQuery findAllByDynamicQuery,
        DynamicQuery findAllPagedDynamicQuery) {
        super(entityClass, entityManager);
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.findOneByDynamicQuery = findOneByDynamicQuery;
        this.findAllByDynamicQuery = findAllByDynamicQuery;
        this.findAllPagedDynamicQuery = findAllPagedDynamicQuery;
    }

    @Override
    public Optional<T> findOneBy(DynamicQueryParams dynamicQuery) {
        String query = createQuery(dynamicQuery, findOneByDynamicQuery, "findOneBy");
        List<T> allContent = findAll(dynamicQuery, query, -1, findOneByDynamicQuery, "findOneBy").getContent();
        return allContent.isEmpty() ? Optional.empty() : Optional.of(allContent.get(0));
    }

    @Override
    public List<T> findAllBy(DynamicQueryParams dynamicQuery) {
        String query = createQuery(dynamicQuery, findAllByDynamicQuery, "findAllBy");
        return findAll(dynamicQuery, query, -1, findAllByDynamicQuery, "findAllBy").getContent();
    }

    @Override
    public Page<T> findAllPaged(DynamicQueryParams dynamicQuery, Pageable pageable) {
        long totalRecords = -1;

        if (pageable != null) {
            String countAllPagedQuery = createCountQuery(dynamicQuery, findAllPagedDynamicQuery, "findAllPaged");

            totalRecords = countBy(
                dynamicQuery,
                countAllPagedQuery,
                findAllPagedDynamicQuery,
                "clazz"
            );

            if (totalRecords < 1) {
                return Page.empty();
            }
        }

        String findAllPagedQuery = createQuery(dynamicQuery, findAllPagedDynamicQuery, "findAllPaged");
        return findAll(dynamicQuery, findAllPagedQuery, totalRecords, findAllPagedDynamicQuery, "findAllPaged");
    }

    private Page<T> findAll(DynamicQueryParams params, String query, long totalRecords, DynamicQuery dynamicQuery, String prefix) {
        TypedQuery<T> typedQuery = createQueryPageableOf(query, params, dynamicQuery, prefix);
        List<T> resultList = typedQuery.getResultList();

        if (totalRecords == -1) {
            totalRecords = resultList.size();
        }

        if (params.isPageable()) {
            Pageable pageable = params.getPageable();
            int pageNumber = pageable.getPageNumber();
            int pageSize = pageable.getPageSize();
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, pageable.getSort());
            return totalRecords > 0 ? new PageImpl<>(resultList, pageRequest, totalRecords) : Page.empty(pageRequest);
        }

        if (totalRecords == 0) {
            return Page.empty();
        }

        PageRequest pageRequest = PageRequest.of(0, (int) totalRecords, params.getSort());
        return totalRecords > 0 ? new PageImpl<>(resultList, pageRequest, totalRecords) : Page.empty(pageRequest);
    }

    private TypedQuery<T> createQueryPageableOf(String query, DynamicQueryParams params, DynamicQuery dynamicQuery, String prefix) {
        TypedQuery<T> typedQuery = createQueryOf(params, query, entityClass, dynamicQuery, prefix);

        if (params.isPageable()) {
            createPageable(typedQuery, params);
        }

        return typedQuery;
    }

    <E> TypedQuery<E> createQueryOf(DynamicQueryParams params, String query, Class<E> returnType, DynamicQuery dynamicQuery, String prefix) {
        TypedQuery<E> typedQuery = entityManager.createQuery(query, returnType);

        params.getParameters().forEach((key, value) -> {
            QueryExpressionKey expressionKey = new FilterExpressionKeyImpl(prefix, key);
            Optional<QueryExpression> filterValue = dynamicQuery.getExpression(expressionKey);
            filterValue.ifPresent(queryExpression -> setQueryParameter(params, typedQuery, queryExpression));
        });

        return typedQuery;
    }

    private <E> void setQueryParameter(DynamicQueryParams dynamicQuery, TypedQuery<E> typedQuery, QueryExpression expression) {
        Object transformed;

        if (PATTERN_IN_CLAUSE.matcher(expression.getClause()).matches()) {
            transformed = expression.getMatcher().apply(dynamicQuery.getObjects(expression.getBinding()));
        } else {
            transformed = expression.getMatcher().apply(dynamicQuery.getObject(expression.getBinding()));
        }

        typedQuery.setParameter(expression.getBinding(), transformed);
    }

    private long countBy(DynamicQueryParams params, String query, DynamicQuery dynamicQuery, String prefix) {
        TypedQuery<Long> typedQuery = createQueryOf(params, query, Long.class, dynamicQuery, prefix);
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

    private String createQuery(DynamicQueryParams params, DynamicQuery dynamicQuery, String prefix) {
        DynamicQueryBuilderImpl queryBuilder = new DynamicQueryBuilderImpl(dynamicQuery);
        return queryBuilder.select().join(params).where(params, prefix).sorted(params).query();
    }

    private String createCountQuery(DynamicQueryParams params, DynamicQuery dynamicQuery, String prefix) {
        DynamicQueryBuilderImpl queryBuilder = new DynamicQueryBuilderImpl(dynamicQuery);
        return queryBuilder.count().join(params).where(params, prefix).query();
    }

}
