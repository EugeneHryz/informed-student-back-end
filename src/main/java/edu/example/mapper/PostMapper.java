package edu.example.mapper;

import edu.example.dto.post.PostResponseDto;
import edu.example.model.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {FileModelMapper.class, UserMapper.class})
public interface PostMapper {

    PostResponseDto toPostResponseDto(Post post);
}
