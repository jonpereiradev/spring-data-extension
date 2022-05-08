package com.github.jonpereiradev.dynamic.jpa.converter;

import java.lang.reflect.InvocationTargetException;

public class EnumTypeConverter<T extends Enum<T>> implements TypeConverter<T> {

    private final Class<?> enumClass;

    public EnumTypeConverter(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T convertValue(String value) {
        try {
            return (T) enumClass.getMethod("valueOf", String.class).invoke(null, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
