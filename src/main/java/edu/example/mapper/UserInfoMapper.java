package edu.example.mapper;

import edu.example.dto.userInfo.UserInfoCreateUpdateDto;
import edu.example.dto.userInfo.UserInfoResponseDto;
import edu.example.model.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Date;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {

    @Mapping(target = "dateOfBirth", qualifiedByName = "mapStringToDate")
    @Mapping(target = "userImage", source = "userImageFileName")
    UserInfo toUserInfo(UserInfoCreateUpdateDto dto);

    @Mapping(target = "userImageFileName", source = "userImage")
    UserInfoResponseDto toUserInfoResponseDto(UserInfo userInfo);

    @Named("mapStringToDate")
    static Date mapStringToDate(String string) {
        return Date.valueOf(string);
    }

}
