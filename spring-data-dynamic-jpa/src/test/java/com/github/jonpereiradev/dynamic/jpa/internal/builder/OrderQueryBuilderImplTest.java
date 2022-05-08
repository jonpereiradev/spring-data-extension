package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.expression.FilterExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderQueryBuilderImplTest {

    private QueryExpression expression;
    private OrderQueryBuilder orderBuilder;

    @Test
    void must_create_query_with_empty_order_from_expression() {
        orderBuilder = new OrderQueryBuilderImpl(new StringBuilder());
        expression = new QueryExpressionImpl(new FilterExpressionKeyImpl("", "name"), "entity.name desc");

        String query = orderBuilder.by(expression).toString();
        assertEquals("order by entity.name desc", query);
    }

    @Test
    void must_create_query_with_order_from_expression() {
        orderBuilder = new OrderQueryBuilderImpl(new StringBuilder("order by entity.id asc"));
        expression = new QueryExpressionImpl(new FilterExpressionKeyImpl("", "name"), "entity.name desc");

        String query = orderBuilder.by(expression).toString();
        assertEquals("order by entity.id asc, entity.name desc", query);
    }

}