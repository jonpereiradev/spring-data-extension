package com.github.jonpereiradev.dynamic.jpa.converter;

import java.util.ArrayList;
import java.util.List;

public interface TypeConverter<T> {

    T convertValue(String value);

    default List<T> convertValueArray(String[] values) {
        List<T> converted = new ArrayList<>();

        for (String value : values) {
            converted.add(convertValue(value));
        }

        return converted;
    }

}
