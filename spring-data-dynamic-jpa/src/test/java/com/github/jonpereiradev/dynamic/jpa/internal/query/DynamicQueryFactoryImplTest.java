package com.github.jonpereiradev.dynamic.jpa.internal.query;

import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
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

import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class DynamicQueryFactoryImplTest {

    @Mock
    private RepositoryMetadata metadata;

    private DynamicQueryFactory factory;
    private Method findAnyFilter;
    private Method findAnyMethod;

    @BeforeEach
    void before_each() throws NoSuchMethodException {
        findAnyFilter = Repositories.MethodFilter.class.getDeclaredMethod("findAny", DynamicQueryParams.class);
        findAnyMethod = Repositories.MethodJoin.class.getDeclaredMethod("findAny", DynamicQueryParams.class);
    }

    @Test
    void must_create_dynamic_query_with_filter_configured() {
        doReturn(Entities.FieldEntity.class).when(metadata).getDomainType();
        doReturn(Repositories.MethodFilter.class).when(metadata).getRepositoryInterface();

        factory = new DynamicQueryFactoryImpl(metadata);

        DynamicQuery dynamicQuery = factory.newInstance(findAnyFilter);
        QueryExpressionKey key = new FilterExpressionKeyImpl(findAnyFilter.getName(), "id");
        Optional<QueryExpression> expression = dynamicQuery.getExpression(key);

        assertTrue(expression.isPresent());
        assertEquals(key.getValue(), expression.get().getKey().getValue());
    }

    @Test
    void must_create_dynamic_query_with_join_configured() {
        doReturn(Entities.FieldEntity.class).when(metadata).getDomainType();
        doReturn(Repositories.MethodJoin.class).when(metadata).getRepositoryInterface();

        factory = new DynamicQueryFactoryImpl(metadata);

        DynamicQuery dynamicQuery = factory.newInstance(findAnyMethod);
        QueryExpressionKey key = new JoinExpressionKeyImpl(findAnyMethod.getName(), "user");
        Optional<QueryExpression> expression = dynamicQuery.getExpression(key);

        assertTrue(expression.isPresent());
        assertEquals(key.getValue(), expression.get().getKey().getValue());
    }

}