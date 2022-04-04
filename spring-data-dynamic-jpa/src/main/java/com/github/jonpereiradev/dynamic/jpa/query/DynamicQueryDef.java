package com.github.jonpereiradev.dynamic.jpa.query;


public final class DynamicQueryDef {

    private final String query;
    private final String countQuery;
    private final DynamicJoinDef joinDef;
    private final DynamicFilterDef filterDef;

    public DynamicQueryDef(String query, String countQuery, DynamicJoinDef joinDef, DynamicFilterDef filterDef) {
        this.query = query;
        this.countQuery = countQuery;
        this.joinDef = joinDef;
        this.filterDef = filterDef;
    }

    public String getQuery() {
        return query;
    }

    public String getCountQuery() {
        return countQuery;
    }

    public DynamicJoinDef getJoinDef() {
        return joinDef;
    }

    public DynamicFilterDef getFilterDef() {
        return filterDef;
    }
}
