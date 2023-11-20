package edu.example.dto.folder;

import edu.example.model.Subject;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FolderResponseDto {
    private Long id;

    private Subject subject;

    private FolderTypeDto type;
}
