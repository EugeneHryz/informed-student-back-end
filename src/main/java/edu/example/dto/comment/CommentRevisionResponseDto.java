package edu.example.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRevisionResponseDto {

    String revisionType;

    private CommentResponseDto comment;

}
