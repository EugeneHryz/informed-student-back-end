package edu.example.dto.folder;

import edu.example.validation.constraints.FolderTypeConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Folder type (list of available types: '/folder/types')")
    private String type;
}
