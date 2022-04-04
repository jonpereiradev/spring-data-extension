package com.github.jonpereiradev.dynamic.jpa.query;

import com.github.jonpereiradev.dynamic.jpa.Restriction;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public final class DynamicFilterDef {

//

//        if (dynamicSelect != null && dynamicSelect.query().contains("where")) {
//        String query = dynamicSelect.query();
//        int indexOf = query.indexOf("where");
//        return dynamicSelect.query().substring(indexOf);
//    }
//
//        return "where 1 = 1";

    private static final Pattern NAME_PARAMETER_PATTERN = Pattern.compile(".*\\s?:(\\w*)\\s?.*");

    private final Map<String, Restriction> filters = new LinkedHashMap<>();

    void put(String name, Restriction restriction) {
        filters.put(name, restriction);
    }

    public Optional<Restriction> get(String key) {
        return Optional.ofNullable(filters.get(key));
    }

    public boolean contains(String key) {
        return filters.containsKey(key);
    }

    public Map<String, Restriction> getFilters() {
        return Collections.unmodifiableMap(filters);
    }

//    private void createRestrictionsOf(DynamicQuery dynamicQuery, QueryBuilder queryBuilder) {
//        queryBuilder.append(queryRef.getWhere());
//
//        dynamicQuery.getParameters()
//            .entrySet()
//            .stream()
//            .filter(o -> dynamicQuery.isPresent(o.getKey())).forEach(
//                entry -> {
//                    Optional<Restriction> definition = queryRef.getRestriction().getRestrictions()
//                        .values()
//                        .stream()
//                        .filter(restrictDefinition -> restrictDefinition.getName().equalsIgnoreCase(entry.getKey()))
//                        .findFirst();
//
//                    definition.ifPresent(o -> queryBuilder.append(o.getClause()));
//                });
//    }
}
