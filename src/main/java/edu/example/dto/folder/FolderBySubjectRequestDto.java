package edu.example.dto.folder;

import edu.example.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

@Data
@EqualsAndHashCode(callSuper = true)
public class FolderBySubjectRequestDto extends PageDto {
    @NotNull
    @Range
    private Long subjectId;
}
