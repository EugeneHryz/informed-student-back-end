package edu.example.dto.post;

import edu.example.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostByFolderRequestDto extends PageDto {
    @NotNull
    @Range
    private Long folderId;
}
