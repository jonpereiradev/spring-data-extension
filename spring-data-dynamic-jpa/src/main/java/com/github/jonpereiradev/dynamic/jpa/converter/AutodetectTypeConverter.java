package com.github.jonpereiradev.dynamic.jpa.converter;

public final class AutodetectTypeConverter implements TypeConverter<Object> {

    @Override
    public Object convertValue(String value) {
        return value;
    }

}