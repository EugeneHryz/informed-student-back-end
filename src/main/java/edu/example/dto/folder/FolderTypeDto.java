package edu.example.dto.folder;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FolderTypeDto {

    private String type;

    private String displayName;
}
