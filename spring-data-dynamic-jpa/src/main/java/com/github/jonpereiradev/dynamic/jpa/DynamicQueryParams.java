package com.github.jonpereiradev.dynamic.jpa;


import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface DynamicQueryParams {

    String PAGEABLE_PAGE_ATTR = "page";
    String PAGEABLE_SIZE_ATTR = "size";
    String PAGEABLE_SORT_ATTR = "sort";

    static DynamicQueryParams newInstance() {
        return new DynamicQueryParamsImpl();
    }

    Object getObject(String name);

    Object getObject(String name, Object defaultValue);

    List<Object> getObjects(String name);

    String getString(String name);

    String getString(String name, String defaultValue);

    Long getLong(String name);

    Long getLong(String name, Long defaultValue);

    Long getLongRequired(String name);

    Integer getInteger(String name);

    Integer getInteger(String name, Integer defaultValue);

    LocalDate getLocalDate(String name);

    LocalDate getLocalDate(String name, LocalDate defaultValue);

    LocalDateTime getLocalDateTime(String name);

    LocalDateTime getLocalDateTime(String name, LocalDateTime defaultValue);

    boolean isPresent(String name);

    boolean isPageable();

    Pageable getPageable();

    Sort getSort();

    MultiValueMap<String, Object> getParameters();

    void addParameter(String name, Object value);

    void setParameter(String name, Object value);
}
