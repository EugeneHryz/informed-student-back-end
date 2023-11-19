package edu.example.dto.folder;

import edu.example.validation.constraints.FolderTypeConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
public class CreateFolderRequestDto {

    @NotNull
    @Range
    private Long subjectId;

    @FolderTypeConstraint
    private String type;
}
