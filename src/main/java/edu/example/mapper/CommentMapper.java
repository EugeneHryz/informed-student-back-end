package edu.example.mapper;

import edu.example.dto.comment.CommentResponseDto;
import edu.example.dto.user.UserDto;
import edu.example.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "comment", target = "user", qualifiedByName = "commentToUserDto")
    CommentResponseDto toCommentResponseDto(Comment comment, Integer numberOfReplies);

    @Mapping(source = "comment", target = "user", qualifiedByName = "commentToUserDto")
    CommentResponseDto toCommentResponseDto(Comment comment);

    @Named("commentToUserDto")
    default UserDto commentToUserDto(Comment comment) {
        if (comment.isAnonymous()) {
            return null;
        }
        return Mappers.getMapper(UserMapper.class).toUserDto(comment.getUser());
    }
}
