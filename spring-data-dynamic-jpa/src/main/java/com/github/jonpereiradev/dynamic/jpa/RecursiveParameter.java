package com.github.jonpereiradev.dynamic.jpa;


import javax.persistence.Query;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class RecursiveParameter<T> {

    private final T object;
    private final Class<?> clazz;
    private final Map<String, Object> values;
    private final List<String> ignoring;

    private RecursiveParameter(T object) {
        this.object = object;
        this.clazz = object.getClass();
        this.values = new HashMap<>();
        this.ignoring = new ArrayList<>();
    }

    public static <T> RecursiveParameter<T> newInstance(T object) {
        return new RecursiveParameter<>(object);
    }

    public RecursiveParameter<T> ignore(String field, String... fields) {
        ignoring.add(field);

        if (fields != null) {
            ignoring.addAll(Arrays.asList(fields));
        }

        return this;
    }

    public String generate() {
        try {
            return generate(new StringBuilder());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String generate(StringBuilder builder) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (!ignoring.contains(field.getName())) {
                Object value = field.get(object);

                builder.append(" and o.").append(field.getName());

                if (value == null) {
                    builder.append(" is null");
                } else {
                    values.put(field.getName(), value);
                    builder.append(" = :").append(field.getName());
                }
            }
        }

        return builder.toString();
    }

    public void setQueryParameters(Query query) {
        values.forEach(query::setParameter);
    }

}
