package com.github.jonpereiradev.dynamic.jpa.query;

import com.github.jonpereiradev.dynamic.jpa.Restriction;
import com.github.jonpereiradev.dynamic.jpa.matcher.DynamicQueryMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DynamicJoinDef {

    private final Map<String, List<Restriction>> joins = new LinkedHashMap<>();

    void put(String clause, String forQueryParam) {
        List<Restriction> definitions = joins.getOrDefault(forQueryParam, new ArrayList<>());
        definitions.add(new Restriction(forQueryParam, clause, DynamicQueryMatchers::none));
        joins.put(forQueryParam, definitions);
    }

    public Map<String, List<Restriction>> getJoins() {
        return Collections.unmodifiableMap(joins);
    }


}
