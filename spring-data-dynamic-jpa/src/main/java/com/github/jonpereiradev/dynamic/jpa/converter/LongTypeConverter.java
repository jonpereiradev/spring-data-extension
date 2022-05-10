package com.github.jonpereiradev.dynamic.jpa.converter;

final class LongTypeConverter implements TypeConverter<Long> {

    @Override
    public Long convertValue(String value) {
        return Long.valueOf(value);
    }

}
