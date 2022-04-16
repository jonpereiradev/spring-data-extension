package com.github.jonpereiradev.dynamic.jpa.internal.builder;

import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;

final class SelectQueryBuilderImpl implements SelectQueryBuilder {

    private final StringBuilder internal;
    private final QueryInspectorResult result;

    SelectQueryBuilderImpl(StringBuilder internal, QueryInspectorResult result) {
        this.internal = internal;
        this.result = result;
    }

    @Override
    public FromQueryBuilder from() {
        internal.append(" from ");

        for (int i = 0; i < result.getFrom().length; i++) {
            QueryInspectorResult.DynamicFrom from = result.getFrom()[i];

            if (i == 0) {
                internal
                    .append(from.getEntityName())
                    .append(" ")
                    .append(from.getAliasName());
            } else {
                internal
                    .append(", ")
                    .append(from.getEntityName())
                    .append(" ")
                    .append(from.getAliasName());
            }
        }

        return new FromQueryBuilderImpl(new StringBuilder(internal), result);
    }

    @Override
    public String toString() {
        return internal.toString().trim();
    }

}