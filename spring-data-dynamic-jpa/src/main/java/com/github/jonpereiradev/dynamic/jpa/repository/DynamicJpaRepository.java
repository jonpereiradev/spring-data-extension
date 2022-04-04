package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.query.DynamicQuery;
import com.github.jonpereiradev.dynamic.jpa.RecursiveParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;


@NoRepositoryBean
public interface DynamicJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    Optional<T> findOneBy(DynamicQuery dynamicQuery);

    List<T> findAllBy(DynamicQuery dynamicQuery);

    Page<T> findAllPaged(DynamicQuery dynamicQuery, Pageable pageable);

    Optional<T> findEquals(T entity);

    Optional<T> findEquals(T entity, RecursiveParameter<T> recursiveParameter);

    <R> R nativeQuery(DynamicQueryBuilder queryBuilder, Function<R, R> consumer);

    void refresh(T entity);

}
