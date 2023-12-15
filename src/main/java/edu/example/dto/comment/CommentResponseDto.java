package edu.example.dto.comment;

import edu.example.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class CommentResponseDto {
    private Long id;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private UserDto user;

    private String text;

    private Integer numberOfReplies;
}
