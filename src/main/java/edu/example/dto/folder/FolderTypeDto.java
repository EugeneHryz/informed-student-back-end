package edu.example.dto.folder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FolderTypeDto {

    @Schema(description = "Folder type name (en)")
    private String type;

    @Schema(description = "Folder type name (ru)")
    private String displayName;
}
