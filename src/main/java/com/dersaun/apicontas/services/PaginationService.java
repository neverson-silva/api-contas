package com.dersaun.apicontas.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PaginationService<T> {

    Page<T> paginate(List<T> itens, Pageable pageable);

    /**
     * Creates a pageable to be used in pagination
     * @param page
     * @param linesPage
     * @param orderBy
     * @param direction
     * @return
     */
    static Pageable createPageable(Integer page,
                                   Integer linesPage,
                                   String orderBy,
                                   String direction) {

        page = page > 0 ? page - 1 : page;

        return PageRequest.of(page, linesPage, Sort.Direction.valueOf(direction), orderBy);
    }
}
