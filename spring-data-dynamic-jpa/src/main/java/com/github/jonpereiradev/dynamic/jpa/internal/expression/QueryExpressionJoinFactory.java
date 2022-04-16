package com.github.jonpereiradev.dynamic.jpa.internal.expression;


import com.github.jonpereiradev.dynamic.jpa.repository.DynamicJoin;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicJoins;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;


// FIXME implement alias handler
public final class QueryExpressionJoinFactory implements QueryExpressionFactory {

    private final Class<?> repositoryInterface;

    public QueryExpressionJoinFactory(RepositoryMetadata metadata) {
        this.repositoryInterface = metadata.getRepositoryInterface();
    }

    @Override
    public Set<QueryExpression> createExpressions() {
        return createExpressions("");
    }

    @Override
    public Set<QueryExpression> createExpressions(String alias) {
        Set<QueryExpression> queryExpressions = new LinkedHashSet<>();

        if (repositoryInterface.isAnnotationPresent(DynamicJoins.class) || repositoryInterface.isAnnotationPresent(DynamicJoin.class)) {
            for (DynamicJoin annotation : repositoryInterface.getAnnotationsByType(DynamicJoin.class)) {
                queryExpressions.add(QueryExpression.newGlobalExpression(annotation.binding(), annotation.query()));
            }
        }

        return queryExpressions;
    }

    @Override
    public Set<QueryExpression> createExpressions(Method method) {
        Set<QueryExpression> queryExpressions = new LinkedHashSet<>();

        if (method.isAnnotationPresent(DynamicJoins.class) || method.isAnnotationPresent(DynamicJoin.class)) {
            for (DynamicJoin annotation : method.getAnnotationsByType(DynamicJoin.class)) {
                queryExpressions.add(QueryExpression.newExpression(method.getName(), annotation.binding(), annotation.query()));
            }
        }

        return queryExpressions;
    }

}
