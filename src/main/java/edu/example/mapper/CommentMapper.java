package edu.example.mapper;

import edu.example.dto.comment.CommentResponseDto;
import edu.example.dto.comment.CommentRevisionResponseDto;
import edu.example.dto.user.UserDto;
import edu.example.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.history.Revision;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CommentMapper {

    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(source = "comment", target = "user", qualifiedByName = "commentToUserDto")
    CommentResponseDto toCommentResponseDto(Comment comment);

    @Named("commentToUserDto")
    default UserDto commentToUserDto(Comment comment) {
        if (comment.isAnonymous()) {
            return null;
        }
        return Mappers.getMapper(UserMapper.class).toUserDto(comment.getUser());
    }

    @Mapping(source = "entity", target = "comment")
    @Mapping(source = "metadata.revisionType", target = "revisionType")
    CommentRevisionResponseDto toCommentRevisionResponseDto(Revision<Long,Comment> value);

    List<CommentRevisionResponseDto> toCommentRevisionResponseDto(List<Revision<Long, Comment>> revisions);

}
