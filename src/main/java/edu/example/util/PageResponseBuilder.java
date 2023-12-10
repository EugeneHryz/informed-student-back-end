package edu.example.util;

import edu.example.dto.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public class PageResponseBuilder {

    private PageResponseBuilder() {
    }

    public static <E, D> PageResponse<D> of(Page<E> page, Function<E, D> mapper) {
        PageResponse<D> response = new PageResponse<D>();
        response.setPageSize(page.getSize());
        response.setPageNumber(page.getNumber());
        response.setTotalPages(page.getTotalPages());
        response.setTotalSize(page.getTotalElements());

        List<D> content = page.getContent().stream()
                .map(mapper)
                .toList();
        response.setContent(content);
        return response;
    }
}
