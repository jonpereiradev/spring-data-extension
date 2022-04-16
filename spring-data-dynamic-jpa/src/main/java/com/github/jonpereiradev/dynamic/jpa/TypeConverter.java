package com.github.jonpereiradev.dynamic.jpa;


/**
 * Utilitário para auxiliar na conversão dos objetos de um {@link java.sql.ResultSet}.
 */
public final class TypeConverter {

    private TypeConverter() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return objeto {@code query} convertido em um {@link Short}.
     */
    public static Short toShort(Object value) {
        if (value == null) {
            return null;
        }

        return Short.valueOf(value.toString());
    }

    /**
     * @return objeto {@code query} convertido em um {@link Integer}.
     */
    public static Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        return Integer.valueOf(value.toString());
    }

    /**
     * @return objeto {@code query} convertido em um {@link Long}.
     */
    public static Long toLong(Object value) {
        if (value == null) {
            return null;
        }

        return Long.valueOf(value.toString());
    }

    /**
     * @return objeto {@code query} convertido em um {@link Double}.
     */
    public static Double toDouble(Object value) {
        if (value == null) {
            return null;
        }

        return Double.valueOf(value.toString());
    }

    /**
     * @return objeto {@code query} convertido em um {@link Float}.
     */
    public static Float toFloat(Object value) {
        if (value == null) {
            return null;
        }

        return Float.valueOf(value.toString());
    }

    /**
     * @return objeto {@code query} convertido em um {@link String}.
     */
    public static String toString(Object value) {
        if (value == null || value.toString() == null || value.toString().trim().isEmpty()) {
            return null;
        }

        return value.toString();
    }

    /**
     * @return objeto {@code query} convertido em um {@link Boolean}.
     */
    public static Boolean toBoolean(Object value) {
        if (toString(value) == null) {
            return null;
        }

        return "true".equals(toString(value));
    }

}
