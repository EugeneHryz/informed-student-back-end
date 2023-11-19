package edu.example.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;

    private Timestamp createdAt;

    private String text;
}
