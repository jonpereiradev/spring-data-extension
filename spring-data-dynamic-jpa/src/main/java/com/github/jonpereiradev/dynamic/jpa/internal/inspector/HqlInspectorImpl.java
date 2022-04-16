package com.github.jonpereiradev.dynamic.jpa.internal.inspector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HqlInspectorImpl implements QueryInspector {

    static final List<String> QUERY_WHERE = Arrays.asList("and", "or", "(", ")");
    static final List<String> QUERY_ORDER = Arrays.asList("asc", "desc");

    private final HqlDictionary dictionary;
    private final Map<String, String[]> mappedBy;

    HqlInspectorImpl() {
        this.dictionary = new HqlDictionary();
        this.mappedBy = new HashMap<>();
    }

    @Override
    public QueryInspectorResult inspect(String query) {
        String[] clauses = query.split("\\s+");
        QueryInspectorResultImpl result = new QueryInspectorResultImpl();

        int index = 0;

        String keyword;
        StringBuilder keywordBuilder;

        while (index < clauses.length) {
            keyword = clauses[index];

            if (dictionary.isBasicKeyword(keyword)) {
                keywordBuilder = new StringBuilder(keyword.toLowerCase());

                for (int i = index + 1; index < clauses.length; i++) {
                    if (!dictionary.isBasicKeyword(clauses[i])) {
                        break;
                    }

                    keywordBuilder.append(" ").append(clauses[i]);
                    index = i;
                }

                keyword = keywordBuilder.toString();

                if (dictionary.isKeyword(keyword, Keyword.SELECT, Keyword.DISTINCT)) {
                    result.setDistinct(true);
                }

                if (dictionary.isKeyword(keyword, Keyword.SELECT)) {
                    index = inspectSelectClause(clauses, index);
                    continue;
                }

                if (dictionary.isKeyword(keyword, Keyword.FROM)) {
                    index = inspectFromClause(clauses, index);
                    continue;
                }

                if (dictionary.isJoinKeyword(keyword)) {
                    index = inspectJoinClause(keyword, clauses, index);
                    continue;
                }

                if (dictionary.isKeyword(keyword, Keyword.WHERE)) {
                    index = inspectWhereClauses(clauses, index);
                    continue;
                }

                if (dictionary.isKeyword(keyword, Keyword.ORDER, Keyword.BY)) {
                    index = inspectOrderByClauses(clauses, index);
                }
            } else {
                index++;
            }
        }

        List<QueryInspectorResult.DynamicFrom> froms = new ArrayList<>();

        for (String from : mappedBy.getOrDefault(Keyword.FROM.name(), new String[0])) {
            String[] split = from.split("\\s+");
            froms.add(new QueryInspectorResult.DynamicFrom(split[0], split[1]));
        }

        result.setSelect(mappedBy.getOrDefault(Keyword.SELECT.name(), new String[0]));
        result.setFrom(froms.toArray(new QueryInspectorResult.DynamicFrom[0]));
        result.setJoin(mappedBy.getOrDefault(Keyword.JOIN.name(), new String[0]));
        result.setWhere(mappedBy.getOrDefault(Keyword.WHERE.name(), new String[0]));
        result.setOrderBy(mappedBy.getOrDefault(Keyword.ORDER.name(), new String[0]));

        return result;
    }

    private int inspectSelectClause(String[] clauses, int index) {
        List<String> expressions = new ArrayList<>();

        for (index += 1; index < clauses.length; index++) {
            if (dictionary.isBasicKeyword(clauses[index])) {
                break;
            }

            expressions.add(clauses[index].replace(",", "").trim());
        }

        mappedBy.put(Keyword.SELECT.name(), expressions.toArray(new String[0]));

        return index;
    }

    private int inspectFromClause(String[] clauses, int index) {
        StringBuilder expressionBuilder = new StringBuilder();
        List<String> expressions = new ArrayList<>();

        for (index += 1; index < clauses.length; index++) {
            if (dictionary.isBasicKeyword(clauses[index])) {
                break;
            }

            String entityName = clauses[index++];
            String aliasName = clauses[index];

            if (aliasName.endsWith(",")) {
                aliasName = aliasName.replace(",", "");
            }

            expressionBuilder.append(entityName).append(" ").append(aliasName);
            expressions.add(expressionBuilder.toString());
            expressionBuilder = new StringBuilder();
        }

        mappedBy.put(Keyword.FROM.name(), expressions.toArray(new String[0]));

        return index;
    }

    private int inspectJoinClause(String keyword, String[] clauses, int index) {
        StringBuilder expressionBuilder = new StringBuilder(keyword);
        List<String> expressions = new ArrayList<>();

        for (index += 1; index < clauses.length; index++) {
            keyword = clauses[index];

            if (dictionary.isBasicKeyword(keyword)) {
                break;
            }

            expressionBuilder.append(" ").append(keyword);
        }

        expressions.add(expressionBuilder.toString());

        String[] joins = mappedBy.getOrDefault(Keyword.JOIN.name(), new String[0]);
        String[] newJoins = new String[joins.length + expressions.size()];

        System.arraycopy(joins, 0, newJoins, 0, joins.length);
        System.arraycopy(expressions.toArray(new String[0]), 0, newJoins, joins.length, expressions.size());

        mappedBy.put(Keyword.JOIN.name(), newJoins);

        return index;
    }

    private int inspectWhereClauses(String[] clauses, int index) {
        int parenthesis = 0;
        StringBuilder expressionBuilder = new StringBuilder("and");
        List<String> expressions = new ArrayList<>();

        String keyword;

        for (index += 1; index < clauses.length; index++) {
            keyword = clauses[index];

            if (keyword.startsWith("(")) {
                expressionBuilder.append(" ").append(keyword);
                parenthesis++;
            } else if (keyword.endsWith(")")) {
                expressionBuilder.append(" ").append(keyword);

                parenthesis--;

                if (parenthesis == 0) {
                    expressions.add(expressionBuilder.toString().trim());
                    expressionBuilder = new StringBuilder();
                }
            } else if (parenthesis > 0) {
                expressionBuilder.append(" ").append(keyword);
            } else if (parenthesis == 0) {
                if (dictionary.isBasicKeyword(keyword)) {
                    break;
                }

                if (QUERY_WHERE.contains(keyword.toLowerCase())) {
                    expressions.add(expressionBuilder.toString().trim());
                    expressionBuilder = new StringBuilder(keyword);
                    continue;
                }

                expressionBuilder.append(" ").append(keyword);
            }
        }

        if (expressionBuilder.length() > 0) {
            expressions.add(expressionBuilder.toString().trim());
        }

        mappedBy.put(Keyword.WHERE.name(), expressions.toArray(new String[0]));

        return index;
    }

    private int inspectOrderByClauses(String[] clauses, int index) {
        StringBuilder expressionBuilder = new StringBuilder();
        List<String> expressions = new ArrayList<>();

        String keyword;

        for (index += 1; index < clauses.length; index++) {
            keyword = clauses[index];

            if (dictionary.isBasicKeyword(keyword)) {
                break;
            }

            expressionBuilder.append(" ").append(keyword.replace(",", ""));

            if (index + 1 < clauses.length) {
                keyword = clauses[index + 1].toLowerCase().replace(",", "");

                if (QUERY_ORDER.contains(keyword)) {
                    expressionBuilder.append(" ").append(keyword);
                    index++;
                }
            }

            expressions.add(expressionBuilder.toString().trim());
            expressionBuilder = new StringBuilder();
        }

        mappedBy.put(Keyword.ORDER.name(), expressions.toArray(new String[0]));

        return index;
    }

    private static class HqlDictionary {

        private static Set<String> keywords;
        private static Set<String> joinKeywords;

        HqlDictionary() {
            synchronized (this) {
                if (keywords == null) {
                    initialize();
                }
            }
        }

        private void initialize() {
            keywords = new HashSet<>();
            joinKeywords = new HashSet<>();

            Arrays.stream(Keyword.values()).forEach(keyword -> keywords.add(keyword.name()));

            joinKeywords.add(Keyword.JOIN.name());
            joinKeywords.add(Keyword.combine(Keyword.INNER, Keyword.JOIN));
            joinKeywords.add(Keyword.combine(Keyword.LEFT, Keyword.JOIN));
            joinKeywords.add(Keyword.combine(Keyword.RIGHT, Keyword.JOIN));
            joinKeywords.add(Keyword.combine(Keyword.JOIN, Keyword.FETCH));
            joinKeywords.add(Keyword.combine(Keyword.INNER, Keyword.JOIN, Keyword.FETCH));
            joinKeywords.add(Keyword.combine(Keyword.LEFT, Keyword.JOIN, Keyword.FETCH));
            joinKeywords.add(Keyword.combine(Keyword.RIGHT, Keyword.JOIN, Keyword.FETCH));
        }

        public boolean isKeyword(String keyword, Keyword... values) {
            String upperCase = keyword.toUpperCase();

            if (values.length == 0) {
                return false;
            }

            for (Keyword expected : values) {
                if (!upperCase.contains(expected.name())) {
                    return false;
                }
            }

            return true;
        }

        public boolean isBasicKeyword(String keyword) {
            return keywords.contains(keyword.toUpperCase());
        }

        public boolean isJoinKeyword(String keyword) {
            return joinKeywords.contains(keyword.toUpperCase());
        }

    }

    enum Keyword {

        SELECT, DISTINCT, FROM, INNER, LEFT, RIGHT, JOIN, OUTER, FETCH, WHERE, ORDER, BY;

        static String combine(Keyword... keywords) {
            StringBuilder combined = new StringBuilder();

            for (int i = 0; i < keywords.length; i++) {
                if (i == 0) {
                    combined.append(keywords[i].name());
                } else {
                    combined.append(" ").append(keywords[i].name());
                }
            }

            return combined.toString();
        }
    }
}
