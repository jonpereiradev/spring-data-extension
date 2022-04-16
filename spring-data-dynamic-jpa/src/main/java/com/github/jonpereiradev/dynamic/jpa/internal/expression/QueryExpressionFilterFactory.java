package com.github.jonpereiradev.dynamic.jpa.internal.expression;


import com.github.jonpereiradev.dynamic.jpa.repository.DynamicQueryMatchers;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicFilter;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicFilters;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class QueryExpressionFilterFactory implements QueryExpressionFactory {

    private static final Pattern NAME_PARAMETER_PATTERN = Pattern.compile(".*\\s?:(\\w*)\\s?.*");

    private final Class<?> entityClass;
    private final Class<?> repositoryInterface;

    public QueryExpressionFilterFactory(RepositoryMetadata metadata) {
        this.entityClass = metadata.getDomainType();
        this.repositoryInterface = metadata.getRepositoryInterface();
    }

    @Override
    public Set<QueryExpression> createExpressions() {
        return createExpressions(entityClass.getSimpleName().toLowerCase());
    }

    @Override
    public Set<QueryExpression> createExpressions(String alias) {
        Set<QueryExpression> expressions = new LinkedHashSet<>();

        readReflectionFilters(expressions, entityClass, alias);
        readRepositoryFilters(expressions, repositoryInterface, alias);

        return expressions;
    }

    @Override
    public Set<QueryExpression> createExpressions(Method method) {
        Set<QueryExpression> expressions = new LinkedHashSet<>();

        if (method.isAnnotationPresent(DynamicFilters.class) || method.isAnnotationPresent(DynamicFilter.class)) {
            QueryExpression queryExpression;

            for (DynamicFilter annotation : method.getAnnotationsByType(DynamicFilter.class)) {
                String name = createName(annotation);
                String query = annotation.query();

                if (!query.startsWith("and")) {
                    query = "and " + query.trim();
                }

                if (annotation.type().equals(DynamicFilter.Feature.class)) {
                    queryExpression = QueryExpression.newFeature(method.getName(), name, query);
                } else {
                    Function<Object, ?> matcher;

                    if (annotation.type().equals(DynamicFilter.AutoDetectType.class)) {
                        matcher = value -> DynamicQueryMatchers.autodetect(entityClass, value);
                    } else {
                        matcher = value -> DynamicQueryMatchers.from(annotation.type(), value);
                    }

                    queryExpression = QueryExpression.newExpression(method.getName(), name, query, matcher);
                }

                expressions.add(queryExpression);
            }
        }

        return expressions;
    }

    private void readReflectionFilters(Set<QueryExpression> expressions, Class<?> entityClass, String alias) {
        String query;
        String name;
        Function<Object, Object> matcher;

        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                query = "and " + alias + "." + field.getName() + " = :" + field.getName();
                name = createName(query);
                matcher = value -> DynamicQueryMatchers.from(field.getType(), value);
                expressions.add(QueryExpression.newGlobalExpression(name, query, matcher));
            } else if (field.isAnnotationPresent(JoinColumn.class)) {
                query = "and " + alias + "." + field.getName() + ".id = :" + field.getName();
                name = createName(query);
                matcher = value -> DynamicQueryMatchers.from(field.getType(), value);
                expressions.add(QueryExpression.newGlobalExpression(name, query, matcher));
            }
        }

        Class<?> superclass = entityClass.getSuperclass();

        if (superclass != null && superclass.isAnnotationPresent(MappedSuperclass.class)) {
            readReflectionFilters(expressions, superclass, alias);
        }
    }

    // TODO Implement alias replace
    private void readRepositoryFilters(Set<QueryExpression> expressions, Class<?> repositoryInterface, String alias) {
        if (isDynamicFilter(repositoryInterface)) {
            QueryExpression queryExpression;
            String name;
            String query;

            for (DynamicFilter annotation : repositoryInterface.getAnnotationsByType(DynamicFilter.class)) {
                name = createName(annotation);
                query = annotation.query();

                if (!query.startsWith("and")) {
                    query = "and " + query.trim();
                }

                if (annotation.type().equals(DynamicFilter.Feature.class)) {
                    queryExpression = QueryExpression.newGlobalFeature(name, query);
                } else {
                    Function<Object, Object> matcher = value -> DynamicQueryMatchers.from(annotation.type(), value);
                    queryExpression = QueryExpression.newGlobalExpression(name, query, matcher);
                }

                expressions.remove(queryExpression);
                expressions.add(queryExpression);
            }
        }

        for (Class<?> anInterface : repositoryInterface.getInterfaces()) {
            if (isDynamicFilter(anInterface)) {
                readRepositoryFilters(expressions, anInterface, alias);
            }
        }
    }

    private boolean isDynamicFilter(Class<?> clazz) {
        return clazz.isAnnotationPresent(DynamicFilters.class) || clazz.isAnnotationPresent(DynamicFilter.class);
    }

    private String createName(DynamicFilter annotation) {
        if (annotation.binding().isEmpty()) {
            Matcher matcher = NAME_PARAMETER_PATTERN.matcher(annotation.query());

            if (!matcher.matches()) {
                throw new IllegalStateException("Filter invalid to apply on query");
            }

            return matcher.group(1);
        }

        return annotation.binding();
    }

    private String createName(String expression) {
        Matcher matcher = NAME_PARAMETER_PATTERN.matcher(expression);

        if (!matcher.matches()) {
            throw new IllegalStateException("Filter invalid to apply on query");
        }

        return matcher.group(1);
    }

}
