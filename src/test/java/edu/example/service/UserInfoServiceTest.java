package edu.example.service;


import edu.example.TestContext;
import edu.example.dto.auth.RegisterRequestDto;
import edu.example.dto.user.UserRequestDto;
import edu.example.dto.userInfo.UserInfoCreateUpdateDto;
import edu.example.exception.EntityNotFoundException;
import edu.example.exception.UnprocessableEntityException;
import edu.example.model.Gender;
import edu.example.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserInfoServiceTest  extends TestContext {

    @Autowired
    UserInfoService userInfoService;
    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;

    @Autowired
    UserInfoRepository userInfoRepository;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    @AfterEach
    void clear() {
        userInfoRepository.deleteAll();
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createUserInfo() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var dto = new UserRequestDto();
        dto.setIsBanned(false);
        var user = userService.getUsers(dto.toPredicate(), 0, 1).get().findFirst().get();

        // when
        var userInfo = userInfoService.createOrUpdateUserInfo(new UserInfoCreateUpdateDto(
                "2000-01-01", "MALE", 1,
                "specialty", "imageFile"), user);

        // then
        assertEquals("username", userInfo.getUsername());
        assertEquals("imageFile", userInfo.getUserImage());
        assertEquals(Gender.MALE, userInfo.getGender());
        assertEquals(1, userInfo.getCourse());
        assertEquals("specialty", userInfo.getSpecialty());
    }

    @Test
    void getUserInfo() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var dto = new UserRequestDto();
        dto.setIsBanned(false);
        var user = userService.getUsers(dto.toPredicate(), 0, 1).get().findFirst().get();

        userInfoService.createOrUpdateUserInfo(new UserInfoCreateUpdateDto(
                "2000-01-01", "MALE", 1,
                "specialty", "imageFile"), user);

        // when
        var userInfo = userInfoService.getUserInfo("username");

        // then
        assertEquals("username", userInfo.getUsername());
        assertEquals("imageFile", userInfo.getUserImage());
        assertEquals(Gender.MALE, userInfo.getGender());
        assertEquals(1, userInfo.getCourse());
        assertEquals("specialty", userInfo.getSpecialty());
    }

    @Test
    void getUserInfoNotFound() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var dto = new UserRequestDto();
        dto.setIsBanned(false);
        var user = userService.getUsers(dto.toPredicate(), 0, 1).get().findFirst().get();

        userInfoService.createOrUpdateUserInfo(new UserInfoCreateUpdateDto(
                "2000-01-01", "MALE", 1,
                "specialty", "imageFile"), user);

        // when
        assertThrows(EntityNotFoundException.class, () ->
                userInfoService.getUserInfo("notexists"));
    }

    @Test
    void updateUserInfo() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var dto = new UserRequestDto();
        dto.setIsBanned(false);
        var user = userService.getUsers(dto.toPredicate(), 0, 1).get().findFirst().get();

        var userInfo = userInfoService.createOrUpdateUserInfo(new UserInfoCreateUpdateDto(
                "2000-01-01", "MALE", 1,
                "specialty", "imageFile"), user);

        // when
        userInfoService.createOrUpdateUserInfo(new UserInfoCreateUpdateDto(
                "2001-02-02", "FEMALE", 2,
                "newSpecialty", "newFile"), user);

        // then
        userInfo = userInfoService.getUserInfo("username");
        assertEquals("username", userInfo.getUsername());
        assertEquals("newFile", userInfo.getUserImage());
        assertEquals(Gender.FEMALE, userInfo.getGender());
        assertEquals(2, userInfo.getCourse());
        assertEquals("newSpecialty", userInfo.getSpecialty());
    }

}
