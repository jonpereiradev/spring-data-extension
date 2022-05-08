package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


@NoRepositoryBean
public interface DynamicJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    Optional<T> findOneBy(DynamicQueryParams params);

    List<T> findAllBy(DynamicQueryParams params);

    Page<T> findAllPaged(DynamicQueryParams params, Pageable pageable);

}
