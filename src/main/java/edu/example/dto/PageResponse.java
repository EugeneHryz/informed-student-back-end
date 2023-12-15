package edu.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Data
public class PageResponse<T> {

    @Schema(description = "Number of pages")
    private int totalPages;

    @Schema(description = "Total number of elements (on all pages)")
    private long totalSize;

    @Schema(description = "Current page number")
    private int pageNumber;

    @Schema(description = "Page max size")
    private int pageSize;

    private List<T> content;

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
