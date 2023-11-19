package edu.example.dto.fileModel;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileModelResponseDto {

    private String originalName;

    private String savedByName;
}
