package com.github.jonpereiradev.dynamic.jpa.internal;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueryProcessorTest {

    private QueryInspect queryProcessor;

    @Test
    public void must_identify_fields() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity");
        assertEquals("[entity.name, entity.date]", Arrays.toString(queryProcessor.getFields()));
    }

    @Test
    public void must_identify_alias() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity");
        assertEquals("entity", queryProcessor.getAliasName());
    }

    @Test
    public void must_identify_entity() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity");
        assertEquals("TestEntity", queryProcessor.getEntityName());
    }

    @Test
    public void must_identify_join() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity join entity.user user");
        assertEquals("[join entity.user user]", Arrays.toString(queryProcessor.getJoins()));
    }

    @Test
    public void must_identify_inner_join() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity inner join entity.user user");
        assertEquals("[inner join entity.user user]", Arrays.toString(queryProcessor.getJoins()));
    }

    @Test
    public void must_identify_left_join() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity left join entity.user user");
        assertEquals("[left join entity.user user]", Arrays.toString(queryProcessor.getJoins()));
    }

    @Test
    public void must_identify_right_join() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity right join entity.user user");
        assertEquals("[right join entity.user user]", Arrays.toString(queryProcessor.getJoins()));
    }

    @Test
    public void must_identify_join_fetch() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity join fetch entity.user user");
        assertEquals("[join fetch entity.user user]", Arrays.toString(queryProcessor.getJoins()));
    }

    @Test
    public void must_identify_inner_join_fetch() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity inner join fetch entity.user user");
        assertEquals("[inner join fetch entity.user user]", Arrays.toString(queryProcessor.getJoins()));
    }

    @Test
    public void must_identify_left_join_fetch() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity left join fetch entity.user user");
        assertEquals("[left join fetch entity.user user]", Arrays.toString(queryProcessor.getJoins()));
    }

    @Test
    public void must_identify_right_join_fetch() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity right join fetch entity.user user");
        assertEquals("[right join fetch entity.user user]", Arrays.toString(queryProcessor.getJoins()));
    }

    @Test
    public void must_identify_where() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity where entity.name is not null and user.id is not null");
        assertEquals("[entity.name is not null, and user.id is not null]", Arrays.toString(queryProcessor.getWhere()));
    }

    @Test
    public void must_identify_order() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity order by entity.name, entity.date desc");
        assertEquals("[entity.name, entity.date desc]", Arrays.toString(queryProcessor.getOrderBy()));
    }

    @Test
    public void must_identify_distinct() {
        queryProcessor = QueryProcessorImpl.process("select distinct entity.name, entity.date from TestEntity entity");
        assertTrue(queryProcessor.isDistinct());
    }

    @Test
    public void must_identify_not_distinct() {
        queryProcessor = QueryProcessorImpl.process("select entity.name, entity.date from TestEntity entity");
        assertFalse(queryProcessor.isDistinct());
    }

}