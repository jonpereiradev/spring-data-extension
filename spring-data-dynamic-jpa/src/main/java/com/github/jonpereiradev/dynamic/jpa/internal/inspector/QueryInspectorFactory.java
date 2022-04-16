package com.github.jonpereiradev.dynamic.jpa.internal.inspector;

public final class QueryInspectorFactory {

    public static QueryInspector newInspector() {
        return newInspectorBy(QueryInspectType.HQL);
    }

    @SuppressWarnings("SameParameterValue")
    private static QueryInspector newInspectorBy(QueryInspectType type) {
        if (type == QueryInspectType.HQL) {
            return new HqlInspectorImpl();
        }

        throw new UnsupportedOperationException("Inspect type doesn't exists");
    }


    enum QueryInspectType {
        HQL;
    }

}
