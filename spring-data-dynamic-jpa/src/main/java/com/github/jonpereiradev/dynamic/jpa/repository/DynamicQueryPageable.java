package com.github.jonpereiradev.dynamic.jpa.repository;


import com.github.jonpereiradev.dynamic.jpa.DynamicQueryParams;
import org.springframework.data.domain.Pageable;

import javax.persistence.Query;


final class DynamicQueryPageable {

    private DynamicQueryPageable() {
    }

    static void setQueryPageable(Query query, DynamicQueryParams params) {
        Pageable pageable = params.getPageable();
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
