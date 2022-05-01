package com.github.jonpereiradev.dynamic.jpa.internal.query;

import com.github.jonpereiradev.dynamic.jpa.Entities;
import com.github.jonpereiradev.dynamic.jpa.Repositories;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.FilterExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.JoinExpressionKeyImpl;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpression;
import com.github.jonpereiradev.dynamic.jpa.internal.expression.QueryExpressionKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DynamicQueryClassFactoryTest {

    @Mock
    private RepositoryMetadata metadata;

    private DynamicQueryFactory factory;

    @BeforeEach
    void before_each() {
        factory = new DynamicQueryClassFactory(metadata);
    }

    @Test
    void must_create_dynamic_query_with_filter_configured() {
        doReturn(Entities.FieldEntity.class).when(metadata).getDomainType();
        doReturn(Repositories.GlobalFilter.class).when(metadata).getRepositoryInterface();

        DynamicQuery dynamicQuery = factory.newInstance();
        QueryExpressionKey key = new FilterExpressionKeyImpl("id");
        Optional<QueryExpression> expression = dynamicQuery.getExpression(key);

        assertTrue(expression.isPresent());
        assertEquals(key.getValue(), expression.get().getKey().getValue());
    }

    @Test
    void must_create_dynamic_query_with_join_configured() {
        doReturn(Entities.FieldEntity.class).when(metadata).getDomainType();
        doReturn(Repositories.GlobalJoin.class).when(metadata).getRepositoryInterface();

        DynamicQuery dynamicQuery = factory.newInstance();
        QueryExpressionKey key = new JoinExpressionKeyImpl("user");
        Optional<QueryExpression> expression = dynamicQuery.getExpression(key);

        assertTrue(expression.isPresent());
        assertEquals(key.getValue(), expression.get().getKey().getValue());
    }

}