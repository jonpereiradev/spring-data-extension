package com.github.jonpereiradev.dynamic.jpa.internal.expression;


import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotation;
import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotationReader;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicFilter;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicFilters;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicQueryMatchers;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class QueryExpressionFilterFactory implements QueryExpressionFactory {

    private static final Pattern ALIAS_PATTERN = Pattern.compile(".*(?:\\s|\\()(\\w+)\\.\\w+.*");

    private final Class<?> entityClass;
    private final Class<?> repositoryInterface;
    private final JpaAnnotationReader reader;

    public QueryExpressionFilterFactory(RepositoryMetadata metadata, JpaAnnotationReader reader) {
        this.entityClass = metadata.getDomainType();
        this.repositoryInterface = metadata.getRepositoryInterface();
        this.reader = reader;
    }

    @Override
    public Set<QueryExpression> createExpressions() {
        return createExpressions(entityClass.getSimpleName().toLowerCase());
    }

    @Override
    public Set<QueryExpression> createExpressions(String alias) {
        Set<QueryExpression> expressions = new LinkedHashSet<>();

        readReflectionFilters(expressions, entityClass, alias);
        readRepositoryFilters(expressions, repositoryInterface, alias, null);

        return expressions;
    }

    @Override
    public Set<QueryExpression> createExpressions(Method method) {
        Set<QueryExpression> expressions = new LinkedHashSet<>();
        String alias = entityClass.getSimpleName().toLowerCase();

        if (method.isAnnotationPresent(Query.class)) {
            QueryInspector inspector = QueryInspectorFactory.newInspector();
            QueryInspectorResult result = inspector.inspect(method.getAnnotation(Query.class).value());
            alias = result.getFrom()[0].getAliasName();
        }

        readRepositoryFilters(expressions, repositoryInterface, alias, method);

        for (DynamicFilter annotation : method.getAnnotationsByType(DynamicFilter.class)) {
            QueryExpression queryExpression = createQueryExpression(annotation, null, method);
            expressions.add(queryExpression);
        }

        return expressions;
    }

    private void readReflectionFilters(Set<QueryExpression> expressions, Class<?> entityClass, String alias) {
        String query;
        String name;
        Function<Object, Object> matcher;

        for (final JpaAnnotation<?> jpaAnnotation : reader.findJpaAnnotations()) {
            if (jpaAnnotation.isAnnotation(JoinColumn.class)) {
                query = "and " + alias + "." + jpaAnnotation.getName() + ".id = :" + jpaAnnotation.getName();
                name = jpaAnnotation.getName();
                matcher = value -> DynamicQueryMatchers.from(jpaAnnotation.getReturnType(), value);
                expressions.add(QueryExpression.newGlobalExpression(name, query, matcher));
            } else {
                query = "and " + alias + "." + jpaAnnotation.getName() + " = :" + jpaAnnotation.getName();
                name = jpaAnnotation.getName();
                matcher = value -> DynamicQueryMatchers.from(jpaAnnotation.getReturnType(), value);
                expressions.add(QueryExpression.newGlobalExpression(name, query, matcher));
            }
        }

        Class<?> superclass = entityClass.getSuperclass();

        if (superclass != null && superclass.isAnnotationPresent(MappedSuperclass.class)) {
            readReflectionFilters(expressions, superclass, alias);
        }
    }

    private void readRepositoryFilters(Set<QueryExpression> expressions, Class<?> repositoryInterface, String alias, Method method) {
        if (isDynamicFilter(repositoryInterface)) {
            for (DynamicFilter annotation : repositoryInterface.getAnnotationsByType(DynamicFilter.class)) {
                QueryExpression queryExpression = createQueryExpression(annotation, alias, method);

                expressions.remove(queryExpression);
                expressions.add(queryExpression);
            }
        }

        for (Class<?> anInterface : repositoryInterface.getInterfaces()) {
            if (isDynamicFilter(anInterface)) {
                readRepositoryFilters(expressions, anInterface, alias, method);
            }
        }
    }

    private QueryExpression createQueryExpression(DynamicFilter annotation, String alias, Method method) {
        QueryExpression queryExpression;

        String name = annotation.binding();
        String query = annotation.query();

        if (!query.startsWith("and")) {
            query = "and " + query.trim();
        }

        if (alias != null && !alias.isEmpty()) {
            String globalAlias = findAlias(query);
            query = query.replaceAll(globalAlias + ".", alias + ".");
        }

        if (method == null) {
            if (annotation.type().equals(DynamicFilter.Feature.class)) {
                queryExpression = QueryExpression.newGlobalFeature(name, query);
            } else {
                Function<Object, Object> matcher = value -> DynamicQueryMatchers.from(annotation.type(), value);
                queryExpression = QueryExpression.newGlobalExpression(name, query, matcher);
            }
        } else {
            if (annotation.type().equals(DynamicFilter.Feature.class)) {
                queryExpression = QueryExpression.newFeature(method.getName(), name, query);
            } else {
                Function<Object, Object> matcher = value -> DynamicQueryMatchers.from(annotation.type(), value);
                queryExpression = QueryExpression.newExpression(method.getName(), name, query, matcher);
            }
        }

        return queryExpression;
    }

    private boolean isDynamicFilter(Class<?> clazz) {
        return clazz.isAnnotationPresent(DynamicFilters.class) || clazz.isAnnotationPresent(DynamicFilter.class);
    }

    private String findAlias(String expression) {
        Matcher matcher = ALIAS_PATTERN.matcher(expression);

        if (!matcher.matches()) {
            throw new IllegalStateException();
        }

        return matcher.group(1);
    }

}
