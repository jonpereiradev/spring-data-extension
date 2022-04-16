package com.github.jonpereiradev.dynamic.jpa;

import com.github.jonpereiradev.dynamic.jpa.repository.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestRepository extends DynamicJpaRepository<TestEntity, String> {

    @SuppressWarnings("JpaQlInspection")
    @Override
    @Query("select entity from TestEntity entity join entity.users user where entity.id is not null")
    List<TestEntity> findAllBy(DynamicQueryParams params);

    @SuppressWarnings("JpaQlInspection")
    @Query(countQuery = "select count(distinct entity.id) from TestEntity entity join entity.users user where entity.id is not null")
    List<TestEntity> countQuery(DynamicQueryParams params);

    @SuppressWarnings("JpaQlInspection")
    @Query
    List<TestEntity> emptyQuery(DynamicQueryParams params);

    @SuppressWarnings("JpaQlInspection")
    @Query("select distinct entity from TestEntity entity join entity.users user where entity.id is not null")
    List<TestEntity> findAllBy2_Distinct(DynamicQueryParams params);

    @SuppressWarnings("JpaQlInspection")
    @Query("select entity from TestEntity entity join fetch entity.users user where entity.id is not null")
    List<TestEntity> findAllBy2_Fetch(DynamicQueryParams params);

    @SuppressWarnings("JpaQlInspection")
    @Query("select entity from TestEntity entity, TestEntity2 entity2 join entity.users user where entity.id = entity2.entityId")
    List<TestEntity> findAllBy2_Multiple(DynamicQueryParams params);

    @SuppressWarnings("JpaQlInspection")
    @Query("select entity from TestEntity entity inner join entity.users user where entity.id is not null")
    List<TestEntity> findAllBy3(DynamicQueryParams params);

    @SuppressWarnings("JpaQlInspection")
    @Query("select entity from TestEntity entity inner join fetch entity.users user where entity.id is not null")
    List<TestEntity> findAllBy3_Fetch(DynamicQueryParams params);

    @SuppressWarnings("JpaQlInspection")
    @Query("select entity from TestEntity entity left join entity.users user where entity.id is not null")
    List<TestEntity> findAllBy4(DynamicQueryParams params);

    @SuppressWarnings("JpaQlInspection")
    @Query("select entity from TestEntity entity left join fetch entity.users user where entity.id is not null")
    List<TestEntity> findAllBy4_Fetch(DynamicQueryParams params);

}
