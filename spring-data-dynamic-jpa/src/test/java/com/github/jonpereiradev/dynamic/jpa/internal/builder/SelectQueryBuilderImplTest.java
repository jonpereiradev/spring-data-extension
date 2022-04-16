package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SelectQueryBuilderImplTest {

    private QueryInspector inspector;
    private QueryInspectorResult result;
    private SelectQueryBuilder builder;

    @BeforeEach
    void before_each() {
        inspector = QueryInspectorFactory.newInspector();
    }

    @Test
    void must_create_query_from() {
        result = inspector.inspect("select entity from TestEntity entity");
        builder = new SelectQueryBuilderImpl(new StringBuilder(), result);
        String query = builder.from().toString();
        assertEquals("from TestEntity entity", query);
    }

    @Test
    void must_create_distinct_query() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select distinct entity from TestEntity entity");
        builder = new SelectQueryBuilderImpl(new StringBuilder(), result);
        String query = builder.from().toString();
        assertEquals("from TestEntity entity", query);
    }

    @Test
    void must_create_multiple_select_query() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity from TestEntity entity, TestEntity2 entity2");
        builder = new SelectQueryBuilderImpl(new StringBuilder(), result);
        String query = builder.from().toString();
        assertEquals("from TestEntity entity, TestEntity2 entity2", query);
    }

    @Test
    void must_create_multiple_fields_select_query() {
        inspector = QueryInspectorFactory.newInspector();
        result = inspector.inspect("select entity.id, entity.name from TestEntity entity");
        builder = new SelectQueryBuilderImpl(new StringBuilder(), result);
        String query = builder.from().toString();
        assertEquals("from TestEntity entity", query);
    }

}