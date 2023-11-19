package edu.example.dto.post;

import edu.example.dto.fileModel.FileModelResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
public class PostResponseDto {
    private Long id;

    private Timestamp createdAt;

    private String text;

    private List<FileModelResponseDto> files;
}
