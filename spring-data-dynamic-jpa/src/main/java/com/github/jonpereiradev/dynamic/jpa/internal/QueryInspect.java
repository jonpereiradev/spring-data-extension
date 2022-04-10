package com.github.jonpereiradev.dynamic.jpa.internal;

public interface QueryInspect {

    String getEntityName();

    String getAliasName();

    boolean isDistinct();

    String[] getFields();

    String[] getJoins();

    String[] getWhere();

    String[] getOrderBy();
}
