package com.github.jonpereiradev.dynamic.jpa.converter;

public class LongTypeConverter implements TypeConverter<Long> {

    @Override
    public Long convertValue(String value) {
        return Long.valueOf(value);
    }

}
