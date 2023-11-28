package edu.example.mapper;

import edu.example.dto.user.UserDto;
import edu.example.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);
}
