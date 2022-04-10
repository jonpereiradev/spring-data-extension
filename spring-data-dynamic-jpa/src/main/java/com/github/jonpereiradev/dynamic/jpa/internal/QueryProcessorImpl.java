package com.github.jonpereiradev.dynamic.jpa.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryProcessorImpl implements QueryInspect {

    private static final List<String> QUERY_WORDS = Arrays.asList("select", "distinct", "from", "join", "inner", "left", "right", "fetch", "where", "order", "by");

    private static final List<String> QUERY_WHERE = Arrays.asList("and", "or");
    private static final List<String> QUERY_ORDER = Arrays.asList("asc", "desc");

    private final String query;
    private final Map<String, String[]> mappedBy;

    QueryProcessorImpl(String query) {
        this.query = query;
        this.mappedBy = new HashMap<>();
    }

    public static QueryInspect process(String query) {
        QueryProcessorImpl processor = new QueryProcessorImpl(query);
        String[] split = query.split("\\s+");

        if (split.length == 0) {
            return processor;
        }

        int index = 0;
        StringBuilder queryWord;

        while (index < split.length) {
            if (QUERY_WORDS.contains(split[index].toLowerCase())) {
                queryWord = new StringBuilder(split[index].toLowerCase());

                for (int i = index + 1; index < split.length; i++) {
                    if (QUERY_WORDS.contains(split[i].toLowerCase())) {
                        queryWord.append(" ").append(split[i]);
                        index = i;
                    } else {
                        break;
                    }
                }

                if ("select distinct".equalsIgnoreCase(queryWord.toString())) {
                    queryWord = new StringBuilder("select");
                    processor.mappedBy.put("distinct", new String[]{"true"});
                }

                List<String> elements = new ArrayList<>();

                if (queryWord.toString().contains("join")) {
                    index++;

                    elements.add(queryWord + " " + split[index++] + " " + split[index++]);
                    processor.mappedBy.put("join", elements.toArray(new String[0]));

                    continue;
                }

                if (queryWord.toString().contains("where")) {
                    StringBuilder condition = new StringBuilder();

                    for (index += 1; index < split.length; index++) {
                        if (QUERY_WHERE.contains(split[index].toLowerCase())) {
                            elements.add(condition.toString().trim());
                            condition = new StringBuilder(split[index]);
                            continue;
                        }

                        if (QUERY_WORDS.contains(split[index].toLowerCase())) {
                            break;
                        }

                        condition.append(" ").append(split[index]);
                    }

                    elements.add(condition.toString().trim());
                    processor.mappedBy.put("where", elements.toArray(new String[0]));

                    continue;
                }

                if (queryWord.toString().contains("order by")) {
                    StringBuilder condition = new StringBuilder();

                    for (index += 1; index < split.length; index++) {
                        if (QUERY_WORDS.contains(split[index].toLowerCase())) {
                            break;
                        }

                        condition.append(" ").append(split[index].replace(",", ""));

                        String nextElement = split[index + 1].toLowerCase().replace(",", "");

                        if (QUERY_ORDER.contains(nextElement)) {
                            condition.append(" ").append(nextElement);
                            index++;
                        }

                        elements.add(condition.toString().trim());
                        condition = new StringBuilder();
                    }

                    processor.mappedBy.put("order_by", elements.toArray(new String[0]));

                    continue;
                }

                for (index += 1; index < split.length; index++) {
                    if (QUERY_WORDS.contains(split[index].toLowerCase())) {
                        break;
                    }

                    elements.add(split[index].replace(",", "").trim());
                }

                processor.mappedBy.put(queryWord.toString(), elements.toArray(new String[0]));
            }
        }

        return processor;
    }

    public String[] getFields() {
        return mappedBy.getOrDefault("select", new String[0]);
    }

    public String getEntityName() {
        return mappedBy.get("from")[0];
    }

    public String getAliasName() {
        return mappedBy.get("from")[1];
    }

    public String[] getJoins() {
        return mappedBy.getOrDefault("join", new String[0]);
    }

    public String[] getWhere() {
        return mappedBy.getOrDefault("where", new String[0]);
    }

    public String[] getOrderBy() {
        return mappedBy.getOrDefault("order_by", new String[0]);
    }

    public boolean isDistinct() {
        return Boolean.parseBoolean(mappedBy.getOrDefault("distinct", new String[]{"false"})[0]);
    }
}
