package com.github.jonpereiradev.dynamic.jpa.converter;

public class NoneTypeConverter implements TypeConverter<Object> {

    @Override
    public Object convertValue(String value) {
        return value;
    }

}
