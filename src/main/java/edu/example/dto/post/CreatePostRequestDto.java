package edu.example.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;


@Data
@AllArgsConstructor
public class CreatePostRequestDto {
    @NotNull
    @Range
    private Long folderId;

    private String text;
}
