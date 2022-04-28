package com.github.jonpereiradev.dynamic.jpa;

import com.github.jonpereiradev.dynamic.jpa.repository.DynamicFilter;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicJoin;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public class Repositories {

    @Repository
    public static class None {
    }

    @Repository
    @DynamicFilter(query = "and filter.id = :id", binding = "id")
    public interface GlobalFilter {
    }

    @Repository
    @DynamicFilter(query = "filter.id = :id", binding = "id")
    public interface GlobalNoAndFilter {
    }

    @Repository
    @DynamicFilter(query = "and filter.id is not null", binding = "id", type = DynamicFilter.Feature.class)
    public interface GlobalFeatureFilter {
    }

    @Repository
    public interface MethodFilter {

        @DynamicFilter(query = "and filter.id = :id", binding = "id")
        List<Entities.Any> findAny(DynamicQueryParams params);

    }

    @Repository
    public interface CombinedFilter extends GlobalFilter {

        @DynamicFilter(query = "and any.name = :name", binding = "name")
        List<Entities.Any> findAny(DynamicQueryParams params);

        @Query("select entity from Any entity")
        @DynamicFilter(query = "and entity.name = :name", binding = "name")
        List<Entities.Any> findQuery(DynamicQueryParams params);

    }

    @Repository
    @DynamicJoin(query = "join any.user user", binding = "user")
    public interface GlobalJoin {
    }

    @Repository
    public interface MethodJoin {

        @DynamicJoin(query = "join any.user user", binding = "user")
        List<Entities.Any> findAny(DynamicQueryParams params);

    }

    @Repository
    public interface CombinedJoin extends GlobalJoin {

        @DynamicJoin(query = "join any.address address", binding = "address")
        List<Entities.Any> findAny(DynamicQueryParams params);

        @Query("select method from Any method")
        @DynamicJoin(query = "join method.address address", binding = "address")
        List<Entities.Any> findQuery(DynamicQueryParams params);

    }

}
