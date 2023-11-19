package edu.example.dto.comment;

import edu.example.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;


@Data
@EqualsAndHashCode(callSuper = true)
public class CommentByPostRequestDto extends PageDto {
    @NotNull
    @Range
    private Long postId;
}
