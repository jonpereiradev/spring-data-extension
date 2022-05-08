package com.github.jonpereiradev.dynamic.jpa.converter;


import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;


public final class DynamicTypeConverter {

    private static final String QUERY_PERCENT = "%";
    private static final Pattern ACCENT_REMOVE_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static final Map<Class<?>, TypeConverter<?>> CONVERTERS = new HashMap<>();

    static {
        CONVERTERS.put(String.class, new StringTypeConverter());
        CONVERTERS.put(short.class, new ShortTypeConverter());
        CONVERTERS.put(Short.class, new ShortTypeConverter());
        CONVERTERS.put(int.class, new IntegerTypeConverter());
        CONVERTERS.put(Integer.class, new IntegerTypeConverter());
        CONVERTERS.put(long.class, new LongTypeConverter());
        CONVERTERS.put(Long.class, new LongTypeConverter());
        CONVERTERS.put(boolean.class, new BooleanTypeConverter());
        CONVERTERS.put(Boolean.class, new BooleanTypeConverter());
        CONVERTERS.put(LocalDate.class, new LocalDateTypeConverter());
        CONVERTERS.put(LocalDateTime.class, new LocalDateTimeTypeConverter());
        CONVERTERS.put(UUID.class, new UUIDTypeConverter());

//        CONVERTERS.put(UUID.class, DynamicTypeConverter::toUUID);
//        CONVERTERS.put(NoAccentMatcher.class, DynamicTypeConverter::noAccent);
//        CONVERTERS.put(NoAccentStartMatcher.class, DynamicTypeConverter::noAccentMatchingStart);
//        CONVERTERS.put(NoAccentEndMatcher.class, DynamicTypeConverter::noAccentMatchingEnd);
//        CONVERTERS.put(NoAccentAnyMatcher.class, DynamicTypeConverter::noAccentMatchingAny);
//        CONVERTERS.put(StartMatcher.class, DynamicTypeConverter::matchingStart);
//        CONVERTERS.put(EndMatcher.class, DynamicTypeConverter::matchingEnd);
//        CONVERTERS.put(AnyMatcher.class, DynamicTypeConverter::matchingAny);
//        CONVERTERS.put(void.class, DynamicTypeConverter::none);
    }


    @SuppressWarnings("unchecked")
    public static <T> TypeConverter<T> get(Class<T> clazz) {
        if (Enum.class.isAssignableFrom(clazz) && !CONVERTERS.containsKey(clazz)) {
            CONVERTERS.put(clazz, new EnumTypeConverter(clazz));
        }

        return (TypeConverter<T>) CONVERTERS.get(clazz);
    }

    public static <T> T convertValue(Class<T> clazz, Object value) {
        if (value == null || value.toString() == null || value.toString().trim().isEmpty()) {
            return null;
        }

        return get(clazz).convertValue(value.toString());
    }

    private static String noAccent(Object value) {
        String normalize = Normalizer.normalize(convertValue(String.class, value), Normalizer.Form.NFD);
        return ACCENT_REMOVE_PATTERN.matcher(normalize).replaceAll("");
    }

    private static String noAccentMatchingStart(Object value) {
        return noAccent(value) + QUERY_PERCENT;
    }

    private static String noAccentMatchingEnd(Object value) {
        return QUERY_PERCENT + noAccent(value);
    }

    private static String noAccentMatchingAny(Object value) {
        return QUERY_PERCENT + noAccent(value) + QUERY_PERCENT;
    }

    private static String matchingStart(Object value) {
        return value + QUERY_PERCENT;
    }

    private static String matchingEnd(Object value) {
        return QUERY_PERCENT + value;
    }

    private static String matchingAny(Object value) {
        return QUERY_PERCENT + value + QUERY_PERCENT;
    }

    private static class NoAccentMatcher {
    }

    private static class NoAccentStartMatcher {
    }

    private static class NoAccentEndMatcher {
    }

    private static class NoAccentAnyMatcher {
    }

    private static class StartMatcher {
    }

    private static class EndMatcher {
    }

    private static class AnyMatcher {
    }

}
