package com.github.jonpereiradev.dynamic.jpa.internal.expression;


import com.github.jonpereiradev.dynamic.jpa.converter.DynamicTypeConverter;
import com.github.jonpereiradev.dynamic.jpa.converter.TypeConverter;
import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotation;
import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotationReader;
import com.github.jonpereiradev.dynamic.jpa.internal.annotation.JpaAnnotationReaderFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspector;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorFactory;
import com.github.jonpereiradev.dynamic.jpa.internal.inspector.QueryInspectorResult;
import com.github.jonpereiradev.dynamic.jpa.repository.AutoScanFilterDisabled;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicFilter;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicFilters;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
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
    public Set<QueryExpression> createExpressions(Method method) {
        Set<QueryExpression> expressions = new LinkedHashSet<>();
        String aliasName = entityClass.getSimpleName().toLowerCase();

        if (method.isAnnotationPresent(Query.class)) {
            QueryInspector inspector = QueryInspectorFactory.newInspector();
            QueryInspectorResult result = inspector.inspect(method.getAnnotation(Query.class).value());
            aliasName = result.getFrom()[0].getAliasName();
        }

        if (!isAutoScanDisabled(method)) {
            readReflectionFilters(expressions, repositoryInterface, aliasName, method);
        }

        readRepositoryFilters(expressions, repositoryInterface, aliasName, method);

        for (DynamicFilter annotation : method.getAnnotationsByType(DynamicFilter.class)) {
            QueryExpression queryExpression = createQueryExpression(annotation, null, method);
            expressions.remove(queryExpression);
            expressions.add(queryExpression);
        }

        return expressions;
    }

    private boolean isAutoScanDisabled(Method method) {
        return repositoryInterface.isAnnotationPresent(AutoScanFilterDisabled.class) || method.isAnnotationPresent(AutoScanFilterDisabled.class);
    }

    private void readReflectionFilters(Set<QueryExpression> expressions, Class<?> entityClass, String aliasName, Method method) {
        String query;

        for (final JpaAnnotation<?> jpaAnnotation : reader.findJpaAnnotations()) {
            if (jpaAnnotation.isAnnotation(JoinColumn.class)) {
                String name = jpaAnnotation.getName();
                Class<?> returnType = jpaAnnotation.getReturnType();
                JpaAnnotationReader joinReader = new JpaAnnotationReaderFactory().createReader(returnType);
                JpaAnnotation<Id> joinAnnotation = joinReader.findFirstNameOf(Id.class);

                query = "and " + aliasName + "." + jpaAnnotation.getName() + "." + joinAnnotation.getName() + " = :" + jpaAnnotation.getName();

                TypeConverter<?> typeConverter = DynamicTypeConverter.get(joinAnnotation.getReturnType());
                expressions.add(newExpression(method.getName(), name, query, typeConverter));
            } else {
                query = "and " + aliasName + "." + jpaAnnotation.getName() + " = :" + jpaAnnotation.getName();
                newExpression(expressions, method, query, jpaAnnotation);
            }
        }

        Class<?> superclass = entityClass.getSuperclass();

        if (superclass != null && superclass.isAnnotationPresent(MappedSuperclass.class)) {
            readReflectionFilters(expressions, superclass, aliasName, method);
        }
    }

    private void newExpression(Set<QueryExpression> expressions, Method method, String query, JpaAnnotation<?> jpaAnnotation) {
        String name = jpaAnnotation.getName();
        TypeConverter<?> typeConverter = DynamicTypeConverter.get(jpaAnnotation.getReturnType());
        expressions.add(newExpression(method.getName(), name, query, typeConverter));
    }

    private void readRepositoryFilters(Set<QueryExpression> expressions, Class<?> repositoryInterface, String aliasName, Method method) {
        if (isDynamicFilter(repositoryInterface)) {
            for (DynamicFilter annotation : repositoryInterface.getAnnotationsByType(DynamicFilter.class)) {
                QueryExpression expression = createQueryExpression(annotation, aliasName, method);

                expressions.remove(expression);
                expressions.add(expression);
            }
        }

        for (Class<?> anInterface : repositoryInterface.getInterfaces()) {
            if (isDynamicFilter(anInterface)) {
                readRepositoryFilters(expressions, anInterface, aliasName, method);
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

        if (!annotation.query().contains(":")) {
            queryExpression = newFeature(method.getName(), name, query);
        } else {
            TypeConverter<?> typeConverter = DynamicTypeConverter.get(annotation.type());
            queryExpression = newExpression(method.getName(), name, query, typeConverter);
        }

        return queryExpression;
    }

    private QueryExpression newExpression(String prefix, String name, String expression, TypeConverter<?> matcher) {
        return new QueryExpressionImpl(new FilterExpressionKeyImpl(prefix, name), expression, matcher);
    }

    private QueryExpression newFeature(String prefix, String name, String expression) {
        return new QueryExpressionImpl(new FilterExpressionKeyImpl(prefix, name), expression, true);
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
