package edu.example.dto.post;

import edu.example.dto.fileModel.FileModelResponseDto;
import edu.example.dto.user.UserDto;
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

    private UserDto user;

    private List<FileModelResponseDto> files;
}
