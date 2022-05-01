package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.expression.FilterExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicQueryMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WhereQueryBuilderImplTest {

    private QueryExpression expression;
    private QueryInspector inspector;
    private QueryInspectorResult result;

    private WhereQueryBuilder whereBuilder;

    @BeforeEach
    void before_each() {
        inspector = QueryInspectorFactory.newInspector();
    }

    @Test
    void must_create_query_with_empty_where_from_expression() {
        result = inspector.inspect("");
        whereBuilder = new WhereQueryBuilderImpl(new StringBuilder(), result);
        expression = new QueryExpressionImpl(new FilterExpressionKeyImpl("", "name"), "and entity.name is not null", DynamicQueryMatchers::none);

        String query = whereBuilder.and(expression).toString();
        assertEquals("where entity.name is not null", query);
    }

    @Test
    void must_create_query_with_where_from_expression() {
        result = inspector.inspect("where entity.id = :id");
        whereBuilder = new WhereQueryBuilderImpl(new StringBuilder("where entity.id = :id"), result);
        expression = new QueryExpressionImpl(new FilterExpressionKeyImpl("", "name"), "and entity.name is not null", DynamicQueryMatchers::none);

        String query = whereBuilder.and(expression).toString();
        assertEquals("where entity.id = :id and entity.name is not null", query);
    }

    @Test
    void must_create_query_with_empty_order_by() {
        result = inspector.inspect("");
        whereBuilder = new WhereQueryBuilderImpl(new StringBuilder(), result);

        String query = whereBuilder.order().toString();
        assertEquals("", query);
    }

    @Test
    void must_create_query_with_order_by() {
        result = inspector.inspect("order by entity.id desc");
        whereBuilder = new WhereQueryBuilderImpl(new StringBuilder(), result);

        String query = whereBuilder.order().toString();
        assertEquals("order by entity.id desc", query);
    }

    @Test
    void must_create_query_with_multiple_order_by() {
        result = inspector.inspect("order by entity.id desc, entity.name asc");
        whereBuilder = new WhereQueryBuilderImpl(new StringBuilder(), result);

        String query = whereBuilder.order().toString();
        assertEquals("order by entity.id desc, entity.name asc", query);
    }

}