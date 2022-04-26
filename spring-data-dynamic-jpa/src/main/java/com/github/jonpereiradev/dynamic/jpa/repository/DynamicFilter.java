package com.github.jonpereiradev.dynamic.jpa.repository;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(DynamicFilters.class)
public @interface DynamicFilter {

    String query();

    String binding();

    Class<?> type() default AutoDetectType.class;

    class AutoDetectType {
    }

    class Feature {
    }

}
