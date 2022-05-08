package com.github.jonpereiradev.dynamic.jpa.internal.expression;

import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import com.github.jonpereiradev.dynamic.jpa.Entities;
import com.github.jonpereiradev.dynamic.jpa.Repositories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
class QueryExpressionJoinFactoryTest {

    @Mock
    private RepositoryMetadata metadata;

    private QueryExpressionFactory expressionFactory;

    @BeforeEach
    void before_each() {
        doReturn(Entities.Any.class).when(metadata).getDomainType();
    }

    @Test
    void must_create_expressions_from_repository_method() throws NoSuchMethodException {
        doReturn(Repositories.MethodJoin.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionJoinFactory(metadata);

        Method method = Repositories.MethodJoin.class.getDeclaredMethod("findAny", DynamicQueryParams.class);
        Set<QueryExpression> expressions = expressionFactory.createExpressions(method);
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals("join." + method.getName() + ".user", expression.getKey().getValue());
        assertEquals("user", expression.getBinding());
        assertEquals("join any.user user", expression.getClause());
    }

    @Test
    void must_create_expressions_from_combined_find_any_method() throws NoSuchMethodException {
        doReturn(Repositories.CombinedJoin.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionJoinFactory(metadata);

        Method method = Repositories.CombinedJoin.class.getDeclaredMethod("findAny", DynamicQueryParams.class);
        List<QueryExpression> expressions = new ArrayList<>(expressionFactory.createExpressions(method));
        assertEquals(2, expressions.size());

        assertEquals("join." + method.getName() + ".user", expressions.get(0).getKey().getValue());
        assertEquals("user", expressions.get(0).getBinding());
        assertEquals("join any.user user", expressions.get(0).getClause());

        assertEquals("join." + method.getName() + ".address", expressions.get(1).getKey().getValue());
        assertEquals("address", expressions.get(1).getBinding());
        assertEquals("join any.address address", expressions.get(1).getClause());
    }

    @Test
    void must_create_expressions_from_combined_find_query_method() throws NoSuchMethodException {
        doReturn(Repositories.CombinedJoin.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionJoinFactory(metadata);

        Method method = Repositories.CombinedJoin.class.getDeclaredMethod("findQuery", DynamicQueryParams.class);
        List<QueryExpression> expressions = new ArrayList<>(expressionFactory.createExpressions(method));
        assertEquals(2, expressions.size());

        assertEquals("join." + method.getName() + ".user", expressions.get(0).getKey().getValue());
        assertEquals("user", expressions.get(0).getBinding());
        assertEquals("join method.user user", expressions.get(0).getClause());

        assertEquals("join." + method.getName() + ".address", expressions.get(1).getKey().getValue());
        assertEquals("address", expressions.get(1).getBinding());
        assertEquals("join method.address address", expressions.get(1).getClause());
    }

}