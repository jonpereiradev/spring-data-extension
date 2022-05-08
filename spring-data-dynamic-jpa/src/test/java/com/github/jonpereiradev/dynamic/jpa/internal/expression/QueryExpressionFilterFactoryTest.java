package com.github.jonpereiradev.dynamic.jpa.internal.expression;

import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import com.github.jonpereiradev.dynamic.jpa.Entities;
import com.github.jonpereiradev.dynamic.jpa.Repositories;
import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotationReader;
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
class QueryExpressionFilterFactoryTest {

    @Mock
    private JpaAnnotationReader reader;

    @Mock
    private RepositoryMetadata metadata;

    private QueryExpressionFactory expressionFactory;

    @BeforeEach
    void before_each() {
        doReturn(Entities.Any.class).when(metadata).getDomainType();
    }

    @Test
    void must_create_expressions_from_method_repository_class() throws NoSuchMethodException {
        doReturn(Repositories.MethodFilter.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Method method = Repositories.MethodFilter.class.getDeclaredMethod("findAny", DynamicQueryParams.class);
        Set<QueryExpression> expressions = expressionFactory.createExpressions(method);

        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        QueryExpressionKey expressionKey = expression.getKey();

        assertEquals("filter." + method.getName() + ".id", expressionKey.getValue());
        assertEquals("id", expression.getBinding());
        assertEquals("and filter.id = :id", expression.getClause());
    }

    @Test
    void must_create_expressions_from_find_any_combined_repository_class() throws NoSuchMethodException {
        doReturn(Repositories.CombinedFilter.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Method method = Repositories.CombinedFilter.class.getDeclaredMethod("findAny", DynamicQueryParams.class);
        List<QueryExpression> expressions = new ArrayList<>(expressionFactory.createExpressions(method));

        assertEquals(2, expressions.size());

        assertEquals("filter." + method.getName() + ".id", expressions.get(0).getKey().getValue());
        assertEquals("id", expressions.get(0).getBinding());
        assertEquals("and any.id = :id", expressions.get(0).getClause());

        assertEquals("filter." + method.getName() + ".name", expressions.get(1).getKey().getValue());
        assertEquals("name", expressions.get(1).getBinding());
        assertEquals("and any.name = :name", expressions.get(1).getClause());
    }

    @Test
    void must_create_expressions_from_find_query_combined_repository_class() throws NoSuchMethodException {
        doReturn(Repositories.CombinedFilter.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Method method = Repositories.CombinedFilter.class.getDeclaredMethod("findQuery", DynamicQueryParams.class);
        List<QueryExpression> expressions = new ArrayList<>(expressionFactory.createExpressions(method));

        assertEquals(2, expressions.size());

        assertEquals("filter." + method.getName() + ".id", expressions.get(0).getKey().getValue());
        assertEquals("id", expressions.get(0).getBinding());
        assertEquals("and entity.id = :id", expressions.get(0).getClause());

        assertEquals("filter." + method.getName() + ".name", expressions.get(1).getKey().getValue());
        assertEquals("name", expressions.get(1).getBinding());
        assertEquals("and entity.name = :name", expressions.get(1).getClause());
    }

}