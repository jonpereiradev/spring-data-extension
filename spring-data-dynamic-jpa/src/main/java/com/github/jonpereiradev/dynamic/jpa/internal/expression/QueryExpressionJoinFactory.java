package com.github.jonpereiradev.dynamic.jpa.internal.expression;


import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicJoin;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;


public final class QueryExpressionJoinFactory implements QueryExpressionFactory {

    private final String defaultAliasName;
    private final Class<?> repositoryInterface;
    private final QueryInspector inspector;

    public QueryExpressionJoinFactory(RepositoryMetadata metadata) {
        this.repositoryInterface = metadata.getRepositoryInterface();
        this.defaultAliasName = metadata.getDomainType().getSimpleName().toLowerCase();
        this.inspector = QueryInspectorFactory.newInspector();
    }

    @Override
    public Set<QueryExpression> createExpressions(Method method) {
        Set<QueryExpression> expressions = new LinkedHashSet<>();
        String aliasName = defaultAliasName;

        if (method.isAnnotationPresent(Query.class)) {
            Query annotation = method.getAnnotation(Query.class);
            QueryInspectorResult result = inspector.inspect(annotation.value());
            aliasName = result.getFrom()[0].getAliasName();
        }

        createExpressions(expressions, repositoryInterface, aliasName, method);

        for (DynamicJoin annotation : method.getAnnotationsByType(DynamicJoin.class)) {
            QueryExpression expression = newExpression(method.getName(), annotation.binding(), annotation.query());
            expressions.add(expression);
        }

        return expressions;
    }

    private void createExpressions(Set<QueryExpression> expressions, Class<?> repositoryInterface, String aliasName, Method method) {
        for (Class<?> anInterface : repositoryInterface.getInterfaces()) {
            if (anInterface.isAnnotationPresent(DynamicJoin.class)) {
                createExpressions(expressions, anInterface, aliasName, method);
            }
        }

        if (repositoryInterface.isAnnotationPresent(DynamicJoin.class)) {
            for (DynamicJoin annotation : repositoryInterface.getAnnotationsByType(DynamicJoin.class)) {
                String query = annotation.query();
                String binding = annotation.binding();

                if (query.contains(defaultAliasName + ".")) {
                    query = query.replaceAll(defaultAliasName + ".", aliasName + ".");
                }

                if (method == null) {
                    QueryExpression expression = newGlobalExpression(binding, query);
                    expressions.add(expression);
                } else {
                    QueryExpression expression = newExpression(method.getName(), binding, query);
                    expressions.add(expression);
                }
            }
        }
    }

    private QueryExpression newGlobalExpression(String name, String expression) {
        return new QueryExpressionImpl(new JoinExpressionKeyImpl(name), expression);
    }

    private QueryExpression newExpression(String prefix, String name, String expression) {
        return new QueryExpressionImpl(new JoinExpressionKeyImpl(prefix, name), expression);
    }

}
