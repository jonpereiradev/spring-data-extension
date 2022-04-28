package com.github.jonpereiradev.dynamic.jpa.internal.expression;

import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import com.github.jonpereiradev.dynamic.jpa.Entities;
import com.github.jonpereiradev.dynamic.jpa.Repositories;
import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotation;
import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotationReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
class QueryExpressionFilterFactoryTest {

    @Mock
    private JpaAnnotationReader reader;

    @Mock
    private RepositoryMetadata metadata;

    private QueryExpressionFactory expressionFactory;
    private JpaAnnotation<Id> jpaAnnotation;

    @BeforeEach
    void before_each() throws NoSuchFieldException {
        Field id = Entities.FieldEntity.class.getDeclaredField("id");
        jpaAnnotation = new JpaAnnotation<>(id, id.getAnnotation(Id.class));

        doReturn(Entities.Any.class).when(metadata).getDomainType();
    }

    @Test
    void must_create_expressions_from_entity_with_default_alias() {
        doReturn(Repositories.None.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        doReturn(singletonList(jpaAnnotation)).when(reader).findJpaAnnotations();

        Set<QueryExpression> expressions = expressionFactory.createExpressions();
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals(QueryExpressionFilterFactory.GLOBAL_PREFIX + "id", expression.getKey());
        assertEquals("id", expression.getBinding());
        assertEquals("and any.id = :id", expression.getClause());
    }

    @Test
    void must_create_expressions_from_entity_with_different_alias() {
        doReturn(Repositories.None.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        doReturn(singletonList(jpaAnnotation)).when(reader).findJpaAnnotations();

        Set<QueryExpression> expressions = expressionFactory.createExpressions("entity");
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals(QueryExpressionFilterFactory.GLOBAL_PREFIX + "id", expression.getKey());
        assertEquals("id", expression.getBinding());
        assertEquals("and entity.id = :id", expression.getClause());
    }

    @Test
    void must_create_expressions_from_join_entity_with_default_alias() throws NoSuchFieldException {
        doReturn(Repositories.None.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Field id = Entities.Join.class.getDeclaredField("none");
        JpaAnnotation<JoinColumn> jpaAnnotation = new JpaAnnotation<>(id, id.getAnnotation(JoinColumn.class));
        doReturn(singletonList(jpaAnnotation)).when(reader).findJpaAnnotations();

        Set<QueryExpression> expressions = expressionFactory.createExpressions();
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals(QueryExpressionFilterFactory.GLOBAL_PREFIX + "none", expression.getKey());
        assertEquals("none", expression.getBinding());
        assertEquals("and any.none.id = :none", expression.getClause());
    }

    @Test
    void must_create_expressions_from_join_entity_with_different_alias() throws NoSuchFieldException {
        doReturn(Repositories.None.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Field id = Entities.Join.class.getDeclaredField("none");
        JpaAnnotation<JoinColumn> jpaAnnotation = new JpaAnnotation<>(id, id.getAnnotation(JoinColumn.class));
        doReturn(singletonList(jpaAnnotation)).when(reader).findJpaAnnotations();

        Set<QueryExpression> expressions = expressionFactory.createExpressions("testing");
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals(QueryExpressionFilterFactory.GLOBAL_PREFIX + "none", expression.getKey());
        assertEquals("none", expression.getBinding());
        assertEquals("and testing.none.id = :none", expression.getClause());
    }

    @Test
    void must_create_expressions_from_repository_class_default_alias() {
        doReturn(Repositories.GlobalFilter.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Set<QueryExpression> expressions = expressionFactory.createExpressions();
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals(QueryExpressionFilterFactory.GLOBAL_PREFIX + "id", expression.getKey());
        assertEquals("id", expression.getBinding());
        assertEquals("and any.id = :id", expression.getClause());
    }

    @Test
    void must_create_expressions_from_repository_class_different_alias() {
        doReturn(Repositories.GlobalFilter.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Set<QueryExpression> expressions = expressionFactory.createExpressions("testing");
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals(QueryExpressionFilterFactory.GLOBAL_PREFIX + "id", expression.getKey());
        assertEquals("id", expression.getBinding());
        assertEquals("and testing.id = :id", expression.getClause());
    }

    @Test
    void must_create_expressions_no_and_from_repository_class_default_alias() {
        doReturn(Repositories.GlobalNoAndFilter.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Set<QueryExpression> expressions = expressionFactory.createExpressions();
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals(QueryExpressionFilterFactory.GLOBAL_PREFIX + "id", expression.getKey());
        assertEquals("id", expression.getBinding());
        assertEquals("and any.id = :id", expression.getClause());
    }

    @Test
    void must_create_expressions_no_and_from_repository_class_different_alias() {
        doReturn(Repositories.GlobalNoAndFilter.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Set<QueryExpression> expressions = expressionFactory.createExpressions("testing");
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals(QueryExpressionFilterFactory.GLOBAL_PREFIX + "id", expression.getKey());
        assertEquals("id", expression.getBinding());
        assertEquals("and testing.id = :id", expression.getClause());
    }

    @Test
    void must_create_expressions_feature_from_repository_class_default_alias() {
        doReturn(Repositories.GlobalFeatureFilter.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Set<QueryExpression> expressions = expressionFactory.createExpressions();
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals(QueryExpressionFilterFactory.GLOBAL_PREFIX + "id", expression.getKey());
        assertEquals("id", expression.getBinding());
        assertEquals("and any.id is not null", expression.getClause());
    }

    @Test
    void must_create_expressions_feature_from_repository_class_different_alias() {
        doReturn(Repositories.GlobalFeatureFilter.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Set<QueryExpression> expressions = expressionFactory.createExpressions("testing");
        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals(QueryExpressionFilterFactory.GLOBAL_PREFIX + "id", expression.getKey());
        assertEquals("id", expression.getBinding());
        assertEquals("and testing.id is not null", expression.getClause());
    }

    @Test
    void must_create_expressions_from_method_repository_class() throws NoSuchMethodException {
        doReturn(Repositories.MethodFilter.class).when(metadata).getRepositoryInterface();
        expressionFactory = new QueryExpressionFilterFactory(metadata, reader);

        Method method = Repositories.MethodFilter.class.getDeclaredMethod("findAny", DynamicQueryParams.class);
        Set<QueryExpression> expressions = expressionFactory.createExpressions(method);

        assertEquals(1, expressions.size());

        QueryExpression expression = expressions.stream().findFirst().orElse(null);
        assertEquals("filter." + method.getName() + ".id", expression.getKey());
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

        assertEquals("filter." + method.getName() + ".id", expressions.get(0).getKey());
        assertEquals("id", expressions.get(0).getBinding());
        assertEquals("and any.id = :id", expressions.get(0).getClause());

        assertEquals("filter." + method.getName() + ".name", expressions.get(1).getKey());
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

        assertEquals("filter." + method.getName() + ".id", expressions.get(0).getKey());
        assertEquals("id", expressions.get(0).getBinding());
        assertEquals("and entity.id = :id", expressions.get(0).getClause());

        assertEquals("filter." + method.getName() + ".name", expressions.get(1).getKey());
        assertEquals("name", expressions.get(1).getBinding());
        assertEquals("and entity.name = :name", expressions.get(1).getClause());
    }

}