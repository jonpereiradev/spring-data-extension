package com.github.jonpereiradev.dynamic.jpa;


/**
 * Utilitário para auxiliar na conversão dos objetos de um {@link java.sql.ResultSet}.
 */
final class TypeConverter {

    private TypeConverter() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return objeto {@code query} convertido em um {@link Short}.
     */
    static Short toShort(Object value) {
        if (value == null) {
            return null;
        }

        return Short.valueOf(value.toString());
    }

    /**
     * @return objeto {@code query} convertido em um {@link Integer}.
     */
    static Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }

        return Integer.valueOf(value.toString());
    }

    /**
     * @return objeto {@code query} convertido em um {@link Long}.
     */
    static Long toLong(Object value) {
        if (value == null) {
            return null;
        }

        return Long.valueOf(value.toString());
    }

    /**
     * @return objeto {@code query} convertido em um {@link Double}.
     */
    static Double toDouble(Object value) {
        if (value == null) {
            return null;
        }

        return Double.valueOf(value.toString());
    }

    /**
     * @return objeto {@code query} convertido em um {@link Float}.
     */
    static Float toFloat(Object value) {
        if (value == null) {
            return null;
        }

        return Float.valueOf(value.toString());
    }

    /**
     * @return objeto {@code query} convertido em um {@link String}.
     */
    static String toString(Object value) {
        if (value == null || value.toString() == null || value.toString().trim().isEmpty()) {
            return null;
        }

        return value.toString();
    }

    /**
     * @return objeto {@code query} convertido em um {@link Boolean}.
     */
    static Boolean toBoolean(Object value) {
        if (toString(value) == null) {
            return null;
        }

        return "true".equals(toString(value));
    }

}
