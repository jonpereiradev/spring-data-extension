package com.github.jonpereiradev.dynamic.jpa.converter;

public final class TypeConverterException extends Exception {

    private final Class<?> type;

    public TypeConverterException(Class<?> type, String message) {
        super(message);
        this.type = type;
    }

}
