package com.github.jonpereiradev.dynamic.jpa.converter;

final class ShortTypeConverter implements TypeConverter<Short> {

    @Override
    public Short convertValue(String value) {
        return Short.valueOf(value);
    }

}
