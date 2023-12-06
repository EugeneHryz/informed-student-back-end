package edu.example.service;


import edu.example.TestContext;
import edu.example.dto.auth.RegisterRequestDto;
import edu.example.exception.EntityNotFoundException;
import edu.example.exception.UnprocessableEntityException;
import edu.example.model.Gender;
import edu.example.model.UserInfo;
import edu.example.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;

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
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        // when
        var userInfo = userInfoService.createOrUpdateUserInfo(new UserInfo("username", "imageFile",
                Date.valueOf("2000-01-01"), Gender.MALE, 1, "specialty"));

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
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        userInfoService.createOrUpdateUserInfo(new UserInfo("username", "imageFile",
                Date.valueOf("2000-01-01"), Gender.MALE, 1, "specialty"));

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
    void getUserImageName() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        userInfoService.createOrUpdateUserInfo(new UserInfo("username", "imageFile",
                Date.valueOf("2000-01-01"), Gender.MALE, 1, "specialty"));

        // when
        var imageName = userInfoService.getUserImageName("username");

        // then
        assertEquals("imageFile", imageName);
    }

    @Test
    void getUserImageNameNotFound() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        userInfoService.createOrUpdateUserInfo(new UserInfo("username", "imageFile",
                Date.valueOf("2000-01-01"), Gender.MALE, 1, "specialty"));

        // when
        assertThrows(EntityNotFoundException.class, () ->
                userInfoService.getUserImageName("notexists"));
    }

    @Test
    void getUserInfoNotFound() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        userInfoService.createOrUpdateUserInfo(new UserInfo("username", "imageFile",
                Date.valueOf("2000-01-01"), Gender.MALE, 1, "specialty"));

        // when
        assertThrows(EntityNotFoundException.class, () ->
                userInfoService.getUserInfo("notexists"));
    }

    @Test
    void createUserInfoNoUser() throws UnprocessableEntityException {
        assertThrows(EntityNotFoundException.class, () ->
                userInfoService.createOrUpdateUserInfo(new UserInfo("username", "imageFile",
                Date.valueOf("2000-01-01"), Gender.MALE, 1, "specialty")));
    }

    @Test
    void updateUserInfo() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var userInfo = userInfoService.createOrUpdateUserInfo(new UserInfo("username",
                "imageFile", Date.valueOf("2000-01-01"), Gender.MALE,
                1, "specialty"));

        // when
        userInfoService.createOrUpdateUserInfo(new UserInfo("username", "newFile",
                Date.valueOf("2001-02-02"), Gender.FEMALE, 2, "newSpecialty"));

        // then
        userInfo = userInfoService.getUserInfo("username");
        assertEquals("username", userInfo.getUsername());
        assertEquals("newFile", userInfo.getUserImage());
        assertEquals(Gender.FEMALE, userInfo.getGender());
        assertEquals(2, userInfo.getCourse());
        assertEquals("newSpecialty", userInfo.getSpecialty());
    }

}
