package com.github.jonpereiradev.dynamic.jpa.query;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryReader;
import com.github.jonpereiradev.dynamic.jpa.Restriction;
import com.github.jonpereiradev.dynamic.jpa.matcher.DynamicQueryMatchers;
import com.github.jonpereiradev.dynamic.jpa.repository.DynamicJpaRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class DynamicFilterDefFactory {

    private final Class<?> entityClass;
    private final Class<?> repositoryInterface;

    public DynamicFilterDefFactory(RepositoryInformation information) {
        this.entityClass = information.getDomainType();
        this.repositoryInterface = information.getRepositoryInterface();
    }

    public DynamicFilterDefFactory(RepositoryMetadata metadata) {
        this.entityClass = metadata.getDomainType();
        this.repositoryInterface = metadata.getRepositoryInterface();
    }

    public DynamicFilterDef createRef() {
        DynamicFilterDef filterDef = new DynamicFilterDef();

        readReflectionFilters(filterDef, entityClass);
        readRepositoryFilters(filterDef, repositoryInterface);
        readMethodsFilters(filterDef, repositoryInterface);

        return filterDef;
    }

    private void readReflectionFilters(DynamicFilterDef filterDef, Class<?> entityClass) {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Class<?> fieldType = field.getType();
                DynamicQueryReader.Mapper mapper = DynamicQueryReader.Mapper.valueOf(fieldType);
                String query = mapper.getQuery(field.getName());
                Function<Object, ?> matcher = mapper.getMatcher(fieldType);
                Builder.clause(query).matcher(matcher).build("clazz", filterDef);
            } else if (field.isAnnotationPresent(JoinColumn.class)) {
                Builder
                    .clause("and o." + field.getName() + ".id = :" + field.getName())
                    .matcher(value -> DynamicQueryMatchers.from(field.getType(), value))
                    .build("clazz", filterDef);
            }
        }

        Class<?> superclass = entityClass.getSuperclass();

        if (superclass != null && superclass.isAnnotationPresent(MappedSuperclass.class)) {
            readReflectionFilters(filterDef, superclass);
        }
    }

    private void readRepositoryFilters(DynamicFilterDef filterDef, Class<?> repositoryInterface) {
        if (isDynamicFilter(repositoryInterface)) {
            DynamicFilter[] annotations = repositoryInterface.getAnnotationsByType(DynamicFilter.class);

            for (DynamicFilter annotation : annotations) {
                Builder
                    .clause(annotation.query())
                    .matcher(value -> DynamicQueryMatchers.from(annotation.type(), value))
                    .build("clazz", filterDef);
            }
        }

        for (Class<?> anInterface : repositoryInterface.getInterfaces()) {
            if (isDynamicFilter(anInterface)) {
                readRepositoryFilters(filterDef, anInterface);
            }
        }
    }

    private void readMethodsFilters(DynamicFilterDef filterDef, Class<?> repositoryInterface) {
        for (Method method : repositoryInterface.getDeclaredMethods()) {
            if (method.isAnnotationPresent(DynamicFilters.class) || method.isAnnotationPresent(DynamicFilter.class)) {
                DynamicFilter[] annotations = method.getAnnotationsByType(DynamicFilter.class);

                for (DynamicFilter annotation : annotations) {
                    Builder
                        .clause(annotation.query())
                        .matcher(value -> DynamicQueryMatchers.from(annotation.type(), value))
                        .build(method.getName().toLowerCase(), filterDef);
                }
            }
        }

        for (Class<?> anInterface : repositoryInterface.getInterfaces()) {
            if (!anInterface.equals(DynamicJpaRepository.class) && !anInterface.equals(JpaRepository.class)) {
                readMethodsFilters(filterDef, anInterface);
            }
        }
    }

    private boolean isDynamicFilter(Class<?> clazz) {
        return clazz.isAnnotationPresent(DynamicFilters.class) || clazz.isAnnotationPresent(DynamicFilter.class);
    }

    static class Builder {

        private static final Pattern NAME_PARAMETER_PATTERN = Pattern.compile(".*\\s?:(\\w*)\\s?.*");

        private String clause;
        private Function<Object, ?> function;

        static Builder clause(String clauseString) {
            Builder builder = new Builder();

            builder.clause = StringUtils.stripToNull(clauseString);
            builder.function = DynamicQueryMatchers::none;

            return builder;
        }

        Builder matcher(Function<Object, ?> function) {
            this.function = function;
            return this;
        }

        void build(String prefix, DynamicFilterDef filterDef) {
            Matcher matcher = NAME_PARAMETER_PATTERN.matcher(clause);

            if (!matcher.matches()) {
                throw new IllegalStateException("Filter invalid to apply on query");
            }

            String name = matcher.group(1);
            filterDef.put(prefix + "." + name, new Restriction(name, clause, function));
        }
    }
}
