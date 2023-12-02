package edu.example.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
public class CreateCommentRequestDto {
    @NotNull
    @Range
    private Long postId;

    @NotNull
    private String text;

    private boolean isAnonymous;
}
