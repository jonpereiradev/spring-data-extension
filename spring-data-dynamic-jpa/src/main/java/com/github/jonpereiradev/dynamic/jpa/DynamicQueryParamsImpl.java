package com.github.jonpereiradev.dynamic.jpa;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;


class DynamicQueryParamsImpl implements DynamicQueryParams {

    MultiValueMap<String, Object> parameters;

    private Pageable pageable;
    private Sort sort;

    DynamicQueryParamsImpl() {
        this.parameters = new LinkedMultiValueMap<>();
    }

    @Override
    public void addParameter(String name, Object value) {
        if (value instanceof List) {
            parameters.addAll(name, (List<?>) value);
        } else if (value instanceof Set) {
            parameters.addAll(name, new ArrayList<>((Set<?>) value));
        } else {
            parameters.add(name, value);
        }
    }

    @Override
    public void setParameter(String name, Object value) {
        parameters.remove(name);
        addParameter(name, value);
    }

    @Override
    public Object getObject(String name) {
        return getObject(name, null);
    }

    @Override
    public Object getObject(String name, Object defaultValue) {
        return isPresent(name) ? parameters.getFirst(name) : defaultValue;
    }

    @Override
    public List<Object> getObjects(String name) {
        Object first = parameters.getFirst(name);

        if (first != null && first.toString().contains(",")) {
            String value = first.toString();
            String[] split = value.split(",");
            return new ArrayList<>(Arrays.asList(split));
        }

        return parameters.getOrDefault(name, Collections.emptyList());
    }

    @Override
    public String getString(String name) {
        return getString(name, null);
    }

    @Override
    public String getString(String name, String defaultValue) {
        return isPresent(name) ? DynamicQueryMatchers.toString(parameters.getFirst(name)) : defaultValue;
    }

    @Override
    public Long getLong(String name) {
        return getLong(name, null);
    }

    @Override
    public Long getLong(String name, Long defaultValue) {
        return isPresent(name) ? DynamicQueryMatchers.toLong(parameters.getFirst(name)) : defaultValue;
    }

    @Override
    public Long getLongRequired(String name) {
        if (!isPresent(name)) {
            throw new IllegalArgumentException(name);
        }

        return DynamicQueryMatchers.toLong(parameters.getFirst(name));
    }

    @Override
    public Integer getInteger(String name) {
        return getInteger(name, null);
    }

    @Override
    public Integer getInteger(String name, Integer defaultValue) {
        return isPresent(name) ? DynamicQueryMatchers.toInteger(parameters.getFirst(name)) : defaultValue;
    }

    @Override
    public Boolean getBoolean(String name) {
        return getBoolean(name, null);
    }

    @Override
    public Boolean getBoolean(String name, Boolean defaultValue) {
        return isPresent(name) ? DynamicQueryMatchers.toBoolean(parameters.getFirst(name)) : defaultValue;
    }

    @Override
    public LocalDate getLocalDate(String name) {
        return getLocalDate(name, null);
    }

    @Override
    public LocalDate getLocalDate(String name, LocalDate defaultValue) {
        return isPresent(name) ? DynamicQueryMatchers.toLocalDate(parameters.getFirst(name)) : defaultValue;
    }

    @Override
    public LocalDateTime getLocalDateTime(String name) {
        return getLocalDateTime(name, null);
    }

    @Override
    public LocalDateTime getLocalDateTime(String name, LocalDateTime defaultValue) {
        return isPresent(name) ? DynamicQueryMatchers.toLocalDateTime(parameters.getFirst(name)) : defaultValue;
    }

    @Override
    public boolean isPresent(String name) {
        return parameters.containsKey(name)
            && parameters.getFirst(name) != null
            && DynamicQueryMatchers.toString(parameters.getFirst(name)) != null;
    }

    @Override
    public boolean isPageable() {
        return isPresent(PAGEABLE_SIZE_ATTR);
    }

    @Override
    public Pageable getPageable() {
        if (pageable != null) {
            return pageable;
        }

        int pageNumber = getInteger(PAGEABLE_PAGE_ATTR, 0);
        int pageSize = getInteger(PAGEABLE_SIZE_ATTR, 10);

        pageable = PageRequest.of(pageNumber, pageSize);

        if (isPresent(PAGEABLE_SORT_ATTR)) {
            pageable = PageRequest.of(pageNumber, pageSize, getSort());
        }

        return pageable;
    }

    @Override
    public Sort getSort() {
        if (sort != null) {
            return sort;
        }

        List<Sort.Order> orders = new ArrayList<>();
        List<Object> object = parameters.getOrDefault(PAGEABLE_SORT_ATTR, Collections.emptyList());

        object.stream().filter(o -> o != null && o.toString() != null && !o.toString().trim().isEmpty()).forEach(o -> {
            String order = o.toString();
            String[] orderAndDirection = order.split(",");

            if (orderAndDirection.length == 1) {
                orders.add(Sort.Order.by(orderAndDirection[0]));
            } else if (orderAndDirection[1].equalsIgnoreCase("asc")) {
                orders.add(Sort.Order.asc(orderAndDirection[0]));
            } else if (orderAndDirection[1].equalsIgnoreCase("desc")) {
                orders.add(Sort.Order.desc(orderAndDirection[0]));
            }
        });

        if (!orders.isEmpty()) {
            sort = Sort.by(orders);
        } else {
            sort = Sort.unsorted();
        }

        return sort;
    }

    @Override
    public MultiValueMap<String, Object> getParameters() {
        return new LinkedMultiValueMap<>(parameters);
    }
}
