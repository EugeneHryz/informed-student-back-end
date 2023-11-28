package edu.example.mapper;

import edu.example.dto.comment.CommentResponseDto;
import edu.example.model.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    CommentResponseDto toCommentResponseDto(Comment comment);
}
