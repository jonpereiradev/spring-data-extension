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

    /**
     * It can be used to specify a type like String.class or Long.class, or it can be used to define a TypeConverter implementation.
     *
     * @return class type or class type converter.
     */
    Class<?> type() default String.class;

}
