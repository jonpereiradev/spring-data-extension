package com.github.jonpereiradev.dynamic.jpa.internal.query;

import com.github.jonpereiradev.dynamic.jpa.internal.expression.FilterExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.JoinExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
class DynamicQueryImplTest {

    @Mock
    private QueryExpression expression;

    private DynamicQuery dynamicQuery;
    private Optional<QueryExpression> id;

    @BeforeEach
    void before_each() {
        dynamicQuery = new DynamicQueryImpl(
            "select entity from Entity entity",
            "select count(entity.id) from Entity entity",
            null,
            null
        );
    }

    @Test
    void must_get_join_by_expression_key() {
        QueryExpressionKey expressionKey = new JoinExpressionKeyImpl("id");

        doReturn(expressionKey).when(expression).getKey();
        dynamicQuery.addExpression(expression);

        id = dynamicQuery.getExpression(expressionKey);

        assertTrue(id.isPresent());
        assertEquals(expressionKey.getValue(), id.get().getKey().getValue());
    }

    @Test
    void must_get_filter_by_expression_key() {
        QueryExpressionKey expressionKey = new FilterExpressionKeyImpl("id");

        doReturn(expressionKey).when(expression).getKey();
        dynamicQuery.addExpression(expression);

        id = dynamicQuery.getExpression(expressionKey);

        assertTrue(id.isPresent());
        assertEquals(expressionKey.getValue(), id.get().getKey().getValue());
    }

}