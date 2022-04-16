package com.github.jonpereiradev.dynamic.jpa.internal.inspector;

public interface QueryInspectorResult {

    boolean isDistinct();

    String[] getSelect();

    DynamicFrom[] getFrom();

    String[] getJoin();

    String[] getWhere();

    String[] getOrderBy();

    class DynamicFrom {

        private final String entityName;
        private final String aliasName;

        public DynamicFrom(String entityName, String aliasName) {
            this.entityName = entityName;
            this.aliasName = aliasName;
        }

        public String getEntityName() {
            return entityName;
        }

        public String getAliasName() {
            return aliasName;
        }

        @Override
        public String toString() {
            return entityName + " " + aliasName;
        }
    }

}
