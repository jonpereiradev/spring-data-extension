package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import com.github.jonpereiradev.dynamic.jpa.TestEntity;
import com.github.jonpereiradev.dynamic.jpa.TestRepository;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;

import static org.junit.jupiter.api.Assertions.*;

class DynamicQueryBuilderImplTest {

    private QueryInspector inspector;
    private QueryInspectorResult result;

    private Query findAllBy;
    private Query countQuery;
    private Query emptyQuery;

    private DynamicQueryBuilder builder;

    @BeforeEach
    void before_each() throws NoSuchMethodException {
        builder = new DynamicQueryBuilderImpl(TestEntity.class);
        findAllBy = TestRepository.class.getDeclaredMethod("findAllBy", DynamicQueryParams.class).getAnnotation(Query.class);
        countQuery = TestRepository.class.getDeclaredMethod("countQuery", DynamicQueryParams.class).getAnnotation(Query.class);
        emptyQuery = TestRepository.class.getDeclaredMethod("emptyQuery", DynamicQueryParams.class).getAnnotation(Query.class);
    }

    @Test
    void must_create_select_query_from_entity() {
        String query = builder.select().toString();
        assertEquals("select testentity", query);
    }

    @Test
    void must_create_count_query_from_entity() {
        String query = builder.count().toString();
        assertEquals("select count(testentity.id)", query);
    }

    @Test
    void must_create_select_query_from_method() {
        String query = builder.select(findAllBy).toString();
        assertEquals("select entity", query);
    }

    @Test
    void must_create_count_query_from_method() {
        String query = builder.count(findAllBy).toString();
        assertEquals("select count(entity.id)", query);
    }

    @Test
    void must_create_select_query_from_empty_method() {
        String query = builder.select(emptyQuery).toString();
        assertEquals("select testentity", query);
    }

    @Test
    void must_create_count_query_from_empty_method() {
        String query = builder.count(emptyQuery).toString();
        assertEquals("select count(testentity.id)", query);
    }

    @Test
    void must_create_count_query_from_count_method() {
        String query = builder.count(countQuery).toString();
        assertEquals("select count(entity.id)", query);
    }

    @Test
    void must_create_select_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity from TestEntity entity");
        String query = builder.select(result).toString();
        assertEquals("select entity", query);
    }

    @Test
    void must_create_count_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity from TestEntity entity");
        String query = builder.count(result).toString();
        assertEquals("select count(entity.id)", query);
    }

    @Test
    void must_create_select_distinct_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select distinct entity from TestEntity entity");
        String query = builder.select(result).toString();
        assertEquals("select distinct entity", query);
    }

    @Test
    void must_create_count_distinct_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select distinct entity from TestEntity entity");
        String query = builder.count(result).toString();
        assertEquals("select count(distinct entity.id)", query);
    }

    @Test
    void must_create_multiple_select_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity from TestEntity entity, TestEntity2 entity2");
        String query = builder.select(result).toString();
        assertEquals("select entity", query);
    }

    @Test
    void must_create_multiple_count_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity from TestEntity entity, TestEntity2 entity2");
        String query = builder.count(result).toString();
        assertEquals("select count(entity.id)", query);
    }

    @Test
    void must_create_select_fields_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity.id, entity.name from TestEntity entity");
        String query = builder.select(result).toString();
        assertEquals("select entity.id, entity.name", query);
    }

    @Test
    void must_create_count_fields_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity.id, entity.name from TestEntity entity, TestEntity2 entity2");
        String query = builder.count(result).toString();
        assertEquals("select count(entity.id)", query);
    }

}