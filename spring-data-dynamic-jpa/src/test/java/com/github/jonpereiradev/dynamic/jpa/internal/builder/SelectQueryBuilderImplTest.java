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

import static org.junit.jupiter.api.Assertions.assertEquals;

class SelectQueryBuilderImplTest {

    private QueryInspector inspector;
    private QueryInspectorResult result;

    private Query findAllBy;

    private SelectQueryBuilder selectBuilder;

    @BeforeEach
    void before_each() throws NoSuchMethodException {
        selectBuilder = new SelectQueryBuilderImpl(new StringBuilder(), "entity");
        findAllBy = TestRepository.class.getDeclaredMethod("findAllBy", DynamicQueryParams.class).getAnnotation(Query.class);
    }

    @Test
    void must_create_query_from_entity() {
        String query = selectBuilder.from(TestEntity.class).getQuery();
        assertEquals("select entity from TestEntity entity", query);
    }

    @Test
    void must_create_query_from_method() {
        String query = selectBuilder.from(TestEntity.class, findAllBy).getQuery();
        assertEquals("select entity from TestEntity entity", query);
    }

    @Test
    void must_create_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity from TestEntity entity");
        String query = selectBuilder.from(TestEntity.class, result).getQuery();
        assertEquals("select entity from TestEntity entity", query);
    }

    @Test
    void must_create_distinct_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select distinct entity from TestEntity entity");
        String query = selectBuilder.from(TestEntity.class, result).getQuery();
        assertEquals("select distinct entity from TestEntity entity", query);
    }

    @Test
    void must_create_multiple_select_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity from TestEntity entity, TestEntity2 entity2");
        String query = selectBuilder.from(TestEntity.class, result).getQuery();
        assertEquals("select entity from TestEntity entity, TestEntity2 entity2", query);
    }

    @Test
    void must_create_multiple_fields_select_query_from_inspector() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity.id, entity.name from TestEntity entity");
        String query = selectBuilder.from(TestEntity.class, result).getQuery();
        assertEquals("select entity.id, entity.name from TestEntity entity", query);
    }

}