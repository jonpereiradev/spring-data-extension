package com.github.jonpereiradev.dynamic.jpa.repository;

import com.github.jonpereiradev.dynamic.jpa.query.DynamicFilterDef;
import org.apache.commons.lang3.StringUtils;

final class FilterDefNameResolver {

    private final DynamicFilterDef filterDef;

    public FilterDefNameResolver(DynamicFilterDef filterDef) {
        this.filterDef = filterDef;
    }

    public String resolve(String key, String prefix) {
        String keyName = StringUtils.lowerCase(prefix) + "." + key;

        if (!filterDef.contains(keyName)) {
            keyName = "clazz." + key;
        }

        return keyName;
    }

}
