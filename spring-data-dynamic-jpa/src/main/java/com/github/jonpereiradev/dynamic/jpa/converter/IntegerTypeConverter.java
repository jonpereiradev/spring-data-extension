package com.github.jonpereiradev.dynamic.jpa.converter;

public class IntegerTypeConverter implements TypeConverter<Integer> {

    @Override
    public Integer convertValue(String value) {
        return Integer.valueOf(value);
    }

}
