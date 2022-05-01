package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.expression.JoinExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicQueryMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FromQueryBuilderImplTest {

    private QueryExpression expression;
    private QueryInspector inspector;
    private QueryInspectorResult result;

    private FromQueryBuilder fromBuilder;

    @BeforeEach
    void before_each() {
        inspector = QueryInspectorFactory.newInspector();
    }

    @Nested
    class JoinExpressionTest {

        @Test
        void must_create_join_query_from_inspector() {
            result = inspector.inspect("join entity.users default");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.join().toString();
            assertEquals("join entity.users default", query);
        }

        @Test
        void must_create_join_fetch_query_from_inspector() {
            result = inspector.inspect("join fetch entity.users default");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.join().toString();
            assertEquals("join fetch entity.users default", query);
        }

        @Test
        void must_create_join_query_from_expression() {
            expression = new QueryExpressionImpl(new JoinExpressionKeyImpl("user"), "join entity.users default", DynamicQueryMatchers::none);
            result = inspector.inspect("");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.join(expression).toString();
            assertEquals("join entity.users default", query);
        }

    }

    @Nested
    class InnerJoinExpressionTest {

        @Test
        void must_create_inner_join_query_from_inspector() {
            result = inspector.inspect("inner join entity.users default");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.join().toString();
            assertEquals("inner join entity.users default", query);
        }

        @Test
        void must_create_inner_join_fetch_query_from_inspector() {
            result = inspector.inspect("inner join fetch entity.users default");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.join().toString();
            assertEquals("inner join fetch entity.users default", query);
        }

        @Test
        void must_create_inner_join_query_from_expression() {
            expression = new QueryExpressionImpl(new JoinExpressionKeyImpl("user"), "inner join entity.users default", DynamicQueryMatchers::none);
            result = inspector.inspect("");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.join(expression).toString();
            assertEquals("inner join entity.users default", query);
        }

    }

    @Nested
    class LeftJoinExpressionTest {

        @Test
        void must_create_left_join_query_from_inspector() {
            result = inspector.inspect("left join entity.users default");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.join().toString();
            assertEquals("left join entity.users default", query);
        }

        @Test
        void must_create_left_join_fetch_query_from_inspector() {
            result = inspector.inspect("left join fetch entity.users default");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.join().toString();
            assertEquals("left join fetch entity.users default", query);
        }

        @Test
        void must_create_left_join_query_from_expression() {
            expression = new QueryExpressionImpl(new JoinExpressionKeyImpl("user"), "left join entity.users default", DynamicQueryMatchers::none);
            result = inspector.inspect("");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.join(expression).toString();
            assertEquals("left join entity.users default", query);
        }

    }

    @Nested
    class WhereExpressionTest {

        @Test
        void must_create_where_query_from_inspector() {
            result = inspector.inspect("where entity.id is not null");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.where().toString();
            assertEquals("where entity.id is not null", query);
        }

        @Test
        void must_create_multiple_where_query_from_inspector() {
            result = inspector.inspect("where entity.id is not null and entity.name <> :name");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.where().toString();
            assertEquals("where entity.id is not null and entity.name <> :name", query);
        }

    }

    @Nested
    class OrderByExpressionTest {

        @Test
        void must_create_empty_order_by_query_from_inspector() {
            result = inspector.inspect("");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.order().toString();
            assertEquals("", query);
        }

        @Test
        void must_create_order_by_query_from_inspector() {
            result = inspector.inspect("order by entity.name");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.order().toString();
            assertEquals("order by entity.name", query);
        }

        @Test
        void must_create_multiple_order_by_query_from_inspector() {
            result = inspector.inspect("order by entity.name desc, entity.id");
            fromBuilder = new FromQueryBuilderImpl(new StringBuilder(), result);
            String query = fromBuilder.order().toString();
            assertEquals("order by entity.name desc, entity.id", query);
        }

    }

}