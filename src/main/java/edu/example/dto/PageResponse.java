package edu.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Setter;

import java.util.List;

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
}
