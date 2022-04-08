package com.github.jonpereiradev.dynamic.jpa;


import java.lang.reflect.InvocationTargetException;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


final class DynamicQueryMatchers {

    private static final Pattern ACCENT_REMOVE_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static final String QUERY_PERCENT = "%";

    static Object none(Object value) {
        return value;
    }

    static <T> Object autodetect(Class<T> clazz, Object value) {
        return value;
    }

    static <T> Object from(Class<T> clazz, Object value) {
        if (value == null || value.toString() == null || value.toString().trim().isEmpty()) {
            return null;
        }

        if (clazz.equals(String.class)) {
            return toString(value);
        }

        if (clazz.equals(String[].class)) {
            return toStringList(value);
        }

        if (clazz.equals(Boolean.class)) {
            return toBoolean(value);
        }

        if (clazz.equals(Short.class)) {
            return toShort(value);
        }

        if (clazz.equals(Short[].class)) {
            return toShortList(value);
        }

        if (clazz.equals(Integer.class)) {
            return toInteger(value);
        }

        if (clazz.equals(Integer[].class)) {
            return toIntegerList(value);
        }

        if (clazz.equals(Long.class)) {
            return toLong(value);
        }

        if (clazz.equals(Long[].class)) {
            return toLongList(value);
        }

        if (clazz.equals(LocalDate.class)) {
            return toLocalDate(value);
        }

        if (clazz.equals(LocalDateTime.class)) {
            return toLocalDateTime(value);
        }

        if (clazz.equals(NoAccentMatcher.class)) {
            return noAccent(value);
        }

        if (clazz.equals(NoAccentStartMatcher.class)) {
            return noAccentMatchingStart(value);
        }

        if (clazz.equals(NoAccentEndMatcher.class)) {
            return noAccentMatchingEnd(value);
        }

        if (clazz.equals(NoAccentAnyMatcher.class)) {
            return noAccentMatchingAny(value);
        }

        if (clazz.equals(StartMatcher.class)) {
            return matchingStart(value);
        }

        if (clazz.equals(EndMatcher.class)) {
            return matchingEnd(value);
        }

        if (clazz.equals(AnyMatcher.class)) {
            return matchingAny(value);
        }

        if (Enum.class.isAssignableFrom(clazz)) {
            return toEnum(clazz, value);
        }

        return none(value);
    }

    @SuppressWarnings("unchecked")
    static <T extends Enum<T>> Enum<T> toEnum(Class<?> enumClass, Object value) {
        try {
            Class<T> clazz = (Class<T>) enumClass;
            return (Enum<T>) clazz.getMethod("valueOf", String.class).invoke(null, value.toString());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    static String toString(Object value) {
        return TypeConverter.toString(value);
    }

    static List<String> toStringList(Object value) {
        List<?> objects = (List<?>) value;
        List<String> strings = new ArrayList<>(objects.size());

        for (Object object : objects) {
            strings.add(TypeConverter.toString(object));
        }

        return strings;
    }

    static Boolean toBoolean(Object value) {
        return TypeConverter.toBoolean(value);
    }

    static Short toShort(Object value) {
        return TypeConverter.toShort(value);
    }

    static List<Short> toShortList(Object value) {
        List<?> objects = (List<?>) value;
        List<Short> shorts = new ArrayList<>(objects.size());

        for (Object object : objects) {
            shorts.add(TypeConverter.toShort(object));
        }

        return shorts;
    }

    static Integer toInteger(Object value) {
        return TypeConverter.toInteger(value);
    }

    static List<Integer> toIntegerList(Object value) {
        List<?> objects = (List<?>) value;
        List<Integer> integers = new ArrayList<>(objects.size());

        for (Object object : objects) {
            integers.add(TypeConverter.toInteger(object));
        }

        return integers;
    }

    static Long toLong(Object value) {
        return TypeConverter.toLong(value);
    }

    static List<Long> toLongList(Object value) {
        List<?> objects = (List<?>) value;
        List<Long> longs = new ArrayList<>(objects.size());

        for (Object object : objects) {
            longs.add(TypeConverter.toLong(object));
        }

        return longs;
    }

    static LocalDate toLocalDate(Object value) {
        if (value == null) {
            return null;
        }

        String date = value.toString();

        if (date.contains("T")) {
            return LocalDate.parse(value.toString(), DateTimeFormatter.ISO_DATE_TIME);
        }

        return LocalDate.parse(value.toString(), DateTimeFormatter.ISO_DATE);
    }

    static LocalDateTime toLocalDateTime(Object value) {
        return value == null ? null : LocalDateTime.parse(value.toString(), DateTimeFormatter.ISO_DATE_TIME);
    }

    static String noAccent(Object value) {
        String normalize = Normalizer.normalize(toString(value), Normalizer.Form.NFD);
        return ACCENT_REMOVE_PATTERN.matcher(normalize).replaceAll("");
    }

    static String noAccentMatchingStart(Object value) {
        return noAccent(value) + QUERY_PERCENT;
    }

    static String noAccentMatchingEnd(Object value) {
        return QUERY_PERCENT + noAccent(value);
    }

    static String noAccentMatchingAny(Object value) {
        return QUERY_PERCENT + noAccent(value) + QUERY_PERCENT;
    }

    static String matchingStart(Object value) {
        return value + QUERY_PERCENT;
    }

    static String matchingEnd(Object value) {
        return QUERY_PERCENT + value;
    }

    static String matchingAny(Object value) {
        return QUERY_PERCENT + value + QUERY_PERCENT;
    }

    static class NoAccentMatcher {
    }

    static class NoAccentStartMatcher {
    }

    static class NoAccentEndMatcher {
    }

    static class NoAccentAnyMatcher {
    }

    static class StartMatcher {
    }

    static class EndMatcher {
    }

    static class AnyMatcher {
    }

}
