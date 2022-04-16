package com.github.jonpereiradev.dynamic.jpa.internal.inspector;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HqlInspectorImplTest {

    private QueryInspector inspector;
    private QueryInspectorResult result;

    @BeforeEach
    void before_each() {
        inspector = new HqlInspectorImpl();
    }

    @Test
    void must_inspect_fields() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity");
        assertEquals("[entity.name, entity.date]", Arrays.toString(result.getSelect()));
    }

    @Test
    void must_inspect_alias() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity");
        assertEquals("entity", result.getFrom()[0].getAliasName());
    }

    @Test
    void must_inspect_entity() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity");
        assertEquals("TestEntity", result.getFrom()[0].getEntityName());
    }

    @Test
    void must_inspect_join() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity join entity.user user");
        assertEquals("[join entity.user user]", Arrays.toString(result.getJoin()));
    }

    @Test
    void must_inspect_inner_join() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity inner join entity.user user");
        assertEquals("[inner join entity.user user]", Arrays.toString(result.getJoin()));
    }

    @Test
    void must_inspect_left_join() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity left join entity.user user");
        assertEquals("[left join entity.user user]", Arrays.toString(result.getJoin()));
    }

    @Test
    void must_inspect_right_join() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity right join entity.user user");
        assertEquals("[right join entity.user user]", Arrays.toString(result.getJoin()));
    }

    @Test
    void must_inspect_join_fetch() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity join fetch entity.user user");
        assertEquals("[join fetch entity.user user]", Arrays.toString(result.getJoin()));
    }

    @Test
    void must_inspect_inner_join_fetch() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity inner join fetch entity.user user");
        assertEquals("[inner join fetch entity.user user]", Arrays.toString(result.getJoin()));
    }

    @Test
    void must_inspect_left_join_fetch() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity left join fetch entity.user user");
        assertEquals("[left join fetch entity.user user]", Arrays.toString(result.getJoin()));
    }

    @Test
    void must_inspect_right_join_fetch() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity right join fetch entity.user user");
        assertEquals("[right join fetch entity.user user]", Arrays.toString(result.getJoin()));
    }

    @Test
    void must_inspect_order() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity order by entity.name, entity.date desc");
        assertEquals("[entity.name, entity.date desc]", Arrays.toString(result.getOrderBy()));
    }

    @Test
    void must_inspect_distinct() {
        result = inspector.inspect("select distinct entity.name, entity.date from TestEntity entity");
        assertTrue(result.isDistinct());
    }

    @Test
    void must_inspect_not_distinct() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity");
        assertFalse(result.isDistinct());
    }

    @Test
    void must_inspect_where() {
        result = inspector.inspect("select entity.name, entity.date from TestEntity entity where entity.name is not null and user.id is not null");
        assertEquals("[and entity.name is not null, and user.id is not null]", Arrays.toString(result.getWhere()));
    }

    @Nested
    class SelectExpressionTest {

        @Test
        void must_inspect_select_with_simple_configuration() {
            result = inspector.inspect("select entity.a");
            assertEquals("[entity.a]", Arrays.toString(result.getSelect()));
            assertFalse(result.isDistinct());
        }

        @Test
        void must_inspect_select_with_complex_configuration() {
            result = inspector.inspect("select entity.a, entity.b");
            assertEquals("[entity.a, entity.b]", Arrays.toString(result.getSelect()));
            assertFalse(result.isDistinct());
        }

        @Test
        void must_inspect_select_distinct_with_simple_configuration() {
            result = inspector.inspect("select distinct entity.a");
            assertEquals("[entity.a]", Arrays.toString(result.getSelect()));
            assertTrue(result.isDistinct());
        }

        @Test
        void must_inspect_select_distinct_with_complex_configuration() {
            result = inspector.inspect("select distinct entity.a, entity.b");
            assertEquals("[entity.a, entity.b]", Arrays.toString(result.getSelect()));
            assertTrue(result.isDistinct());
        }

    }

    @Nested
    class FromExpressionTest {

        @Test
        void must_inspect_from_with_simple_configuration() {
            result = inspector.inspect("from Entity entity");
            assertEquals("[Entity entity]", Arrays.toString(result.getFrom()));
        }

        @Test
        void must_inspect_from_with_complex_configuration() {
            result = inspector.inspect("from Entity entity, Entity2 entity2");
            assertEquals("[Entity entity, Entity2 entity2]", Arrays.toString(result.getFrom()));
        }

    }

    @Nested
    class JoinExpressionTest {

        @Test
        void must_inspect_single_join_with_simple_configuration() {
            result = inspector.inspect("join entity.user user");
            assertEquals("[join entity.user user]", Arrays.toString(result.getJoin()));
        }

        @Test
        void must_inspect_single_join_fetch_with_simple_configuration() {
            result = inspector.inspect("join fetch entity.user user");
            assertEquals("[join fetch entity.user user]", Arrays.toString(result.getJoin()));
        }

        @Test
        void must_inspect_single_join_with_complex_configuration() {
            result = inspector.inspect("join Account a on a.name = entity.accountName or a.id = entity.accountId");
            assertEquals("[join Account a on a.name = entity.accountName or a.id = entity.accountId]", Arrays.toString(result.getJoin()));
        }

        @Test
        void must_inspect_multiple_join_with_simple_configuration() {
            result = inspector.inspect("join entity.user user join entity.account account");
            assertEquals("[join entity.user user, join entity.account account]", Arrays.toString(result.getJoin()));
        }

        @Test
        void must_inspect_multiple_join_with_complex_configuration() {
            result = inspector.inspect("join Account a1 on a1.id = entity.a1 join Address a2 on a2.id = entity.a2");
            assertEquals("[join Account a1 on a1.id = entity.a1, join Address a2 on a2.id = entity.a2]", Arrays.toString(result.getJoin()));
        }

    }

    @Nested
    class WhereExpressionTest {

        @Test
        void must_inspect_where_without_parenthesis() {
            result = inspector.inspect("where entity.name is not null or user.id is not null");
            assertEquals("[and entity.name is not null, or user.id is not null]", Arrays.toString(result.getWhere()));
        }

        @Test
        void must_inspect_where_with_parenthesis() {
            result = inspector.inspect("where (entity.name is not null or entity.value is null)");
            assertEquals("[and (entity.name is not null or entity.value is null)]", Arrays.toString(result.getWhere()));
        }

    }

    @Nested
    class OrderByExpressionTest {

        @Test
        void must_inspect_order_by_with_asc() {
            result = inspector.inspect("order by entity.name asc");
            assertEquals("[entity.name asc]", Arrays.toString(result.getOrderBy()));
        }

        @Test
        void must_inspect_order_by_with_desc() {
            result = inspector.inspect("order by entity.name desc");
            assertEquals("[entity.name desc]", Arrays.toString(result.getOrderBy()));
        }

        @Test
        void must_inspect_order_by_multiple() {
            result = inspector.inspect("order by entity.name asc, entity.id desc, entity.value");
            assertEquals("[entity.name asc, entity.id desc, entity.value]", Arrays.toString(result.getOrderBy()));
        }

    }

}