package com.github.jonpereiradev.dynamic.jpa;


import com.github.jonpereiradev.dynamic.jpa.query.DynamicQuery;
import org.springframework.data.domain.Pageable;

import javax.persistence.Query;


public class DynamicQueryPageable {

    private DynamicQueryPageable() {
    }

    public static void setQueryPageable(Query query, DynamicQuery dynamicQuery) {
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
