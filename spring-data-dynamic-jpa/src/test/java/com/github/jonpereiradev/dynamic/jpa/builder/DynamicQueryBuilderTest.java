package com.github.jonpereiradev.dynamic.jpa.builder;

import com.github.jonpereiradev.dynamic.jpa.TestEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DynamicQueryBuilderTest {

//    @Mock
//    private RepositoryMetadata repositoryMetadata;

    private DynamicQueryBuilder builder;

    @BeforeEach
    public void before_each_test() {
        builder = DynamicQueryBuilder.newInstance();
//        doReturn(TestRepository.class).when(repositoryMetadata).getRepositoryInterface();
//        doReturn(TestEntity.class).when(repositoryMetadata).getDomainType();
    }

    @Test
    public void must_create_basic_select_query_from_entity() {
        FromQueryBuilder example = builder.select("test").from(TestEntity.class);
        assertEquals("select test from TestEntity test", example.getQuery());
    }

    @Test
    public void must_create_basic_count_query_from_entity() {
        FromQueryBuilder example = builder.count("test").from(TestEntity.class);
        assertEquals("select count(test.id) from TestEntity test", example.getQuery());
    }

    @Test
    @Disabled
    public void must_create_basic_select_query_from_method() {
        FromQueryBuilder example = builder.select("example").from(TestEntity.class);
        assertEquals("select example from Example", example.getQuery());
    }

    @Test
    @Disabled
    public void must_create_basic_count_query_from_method() {
        FromQueryBuilder example = builder.select("example").from(TestEntity.class);
        assertEquals("select count(example.id) from Example", example.getQuery());
    }
}