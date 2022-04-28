package com.github.jonpereiradev.dynamic.jpa.internal.inspector;

public interface QueryInspectorResult {

    boolean isDistinct();

    String[] getSelect();

    FromInspected[] getFrom();

    String[] getJoin();

    String[] getWhere();

    String[] getOrderBy();

    class FromInspected {

        private final String entityName;
        private final String aliasName;

        public FromInspected(String entityName, String aliasName) {
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
