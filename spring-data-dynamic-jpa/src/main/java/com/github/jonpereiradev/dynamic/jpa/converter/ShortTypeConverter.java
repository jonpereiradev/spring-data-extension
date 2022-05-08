package com.github.jonpereiradev.dynamic.jpa.converter;

public class ShortTypeConverter implements TypeConverter<Short> {

    @Override
    public Short convertValue(String value) {
        return Short.valueOf(value);
    }

}
