package com.github.jonpereiradev.dynamic.jpa;


import org.springframework.data.domain.Pageable;

import javax.persistence.Query;


final class DynamicQueryPageable {

    private DynamicQueryPageable() {
    }

    static void setQueryPageable(Query query, DynamicQueryParams dynamicQuery) {
        Pageable pageable = dynamicQuery.getPageable();
        int pageNumber = pageable.getPageNumber();

        if (pageNumber < 0) {
            pageNumber = 0;
        }

        if (pageNumber > 0) {
            query.setFirstResult(pageNumber * pageable.getPageSize());
        } else {
            query.setFirstResult(0);
        }

        query.setMaxResults(pageable.getPageSize());
    }

}
