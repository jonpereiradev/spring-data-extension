package com.github.jonpereiradev.dynamic.jpa;

import com.github.jonpereiradev.dynamic.jpa.repository.DynamicFilter;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicJoin;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public class Repositories {

    @Repository
    public static class None {
    }

    @Repository
    @DynamicFilter(query = "and filter.id = :id", binding = "id", type = int.class)
    public interface GlobalFilter extends DynamicJpaRepository<Integer, Entities.Any> {
    }

    @Repository
    @DynamicFilter(query = "filter.id = :id", binding = "id", type = int.class)
    public interface GlobalNoAndFilter extends DynamicJpaRepository<Integer, Entities.Any> {
    }

    @Repository
    @DynamicFilter(query = "and filter.id is not null", binding = "id", type = Boolean.class)
    public interface GlobalFeatureFilter extends DynamicJpaRepository<Integer, Entities.Any> {
    }

    @Repository
    public interface MethodFilter extends DynamicJpaRepository<Integer, Entities.Any> {

        @DynamicFilter(query = "and filter.id = :id", binding = "id", type = int.class)
        List<Entities.Any> findAny(DynamicQueryParams params);

    }

    @Repository
    public interface CombinedFilter extends GlobalFilter {

        @DynamicFilter(query = "and any.name = :name", binding = "name", type = String.class)
        List<Entities.Any> findAny(DynamicQueryParams params);

        /** @noinspection JpaQlInspection*/
        @Query("select entity from Any entity")
        @DynamicFilter(query = "and entity.name = :name", binding = "name", type = String.class)
        List<Entities.Any> findQuery(DynamicQueryParams params);

    }

    @Repository
    @DynamicJoin(query = "join any.user user", binding = "user")
    public interface GlobalJoin extends DynamicJpaRepository<Integer, Entities.Any> {
    }

    @Repository
    public interface MethodJoin extends DynamicJpaRepository<Integer, Entities.Any> {

        @DynamicJoin(query = "join any.user user", binding = "user")
        List<Entities.Any> findAny(DynamicQueryParams params);

    }

    @Repository
    public interface CombinedJoin extends GlobalJoin {

        @DynamicJoin(query = "join any.address address", binding = "address")
        List<Entities.Any> findAny(DynamicQueryParams params);

        /** @noinspection JpaQlInspection*/
        @Query("select method from Any method")
        @DynamicJoin(query = "join method.address address", binding = "address")
        List<Entities.Any> findQuery(DynamicQueryParams params);

    }

}
