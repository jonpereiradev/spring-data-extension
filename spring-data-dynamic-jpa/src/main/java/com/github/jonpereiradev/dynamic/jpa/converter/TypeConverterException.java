package com.github.jonpereiradev.dynamic.jpa.converter;

public final class TypeConverterException extends RuntimeException {

    private final Class<?> type;

    public TypeConverterException(Class<?> type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    public TypeConverterException(Class<?> type, String message) {
        super(message);
        this.type = type;
    }

}
