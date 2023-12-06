package edu.example.service;

import edu.example.TestContext;
import edu.example.dto.auth.RegisterRequestDto;
import edu.example.exception.EntityNotFoundException;
import edu.example.exception.UnprocessableEntityException;
import edu.example.repository.TokenRepository;
import edu.example.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest extends TestContext {

    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;

    @BeforeEach
    @AfterEach
    void clear() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto(
                "mail@address.com", "username", "password"));
        authService.register(new RegisterRequestDto(
                "mail1@address.com", "username1", "password"));
        userService.updateUserBanStatus(
                userService.getUsers(null, 0, 2).get()
                        .filter(user -> user.getUsername().equals("username"))
                        .findAny().get().getId(),
                true);  // Banned 'username'

        // when
        var usersPage = userService.getUsers(null, 0, 2);

        // then
        assertEquals(2, usersPage.getTotalElements());
        assertTrue(usersPage.stream().anyMatch(user -> user.getUsername().equals("username")));
        assertTrue(usersPage.stream().anyMatch(user -> user.getUsername().equals("username1")));
    }

    @Test
    void getAllUsersNotBanned() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto(
                "mail@address.com", "username", "password"));
        authService.register(new RegisterRequestDto(
                "mail1@address.com", "username1", "password"));
        userService.updateUserBanStatus(
                userService.getUsers(null, 0, 2).get()
                        .filter(user -> user.getUsername().equals("username"))
                        .findFirst().get().getId(),
                true);  // Banned 'username'

        // when
        var usersPage = userService.getUsers(false, 0, 2);

        // then
        assertEquals(1, usersPage.getTotalElements());
        assertTrue(usersPage.stream().anyMatch(user -> user.getUsername().equals("username1")));
    }

    @Test
    void deleteUser() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto(
                "mail@address.com", "username", "password"));

        // when
        userService.deleteUser(userService.getUsers(null, 0, 1)
                .get().toList().get(0).getId());

        // then
        var usersPage = userService.getUsers(null, 0, 1);
        assertFalse(usersPage.stream().anyMatch(user -> user.getUsername().equals("username")));
    }

    @Test
    void deleteUserNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                userService.deleteUser(0L));
    }

    @Test
    void updateUserBanStatusNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                userService.updateUserBanStatus(0L, true));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void updateUserBanStatus(boolean banStatus) throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto(
                "mail@address.com", "username", "password"));

        // when
        userService.updateUserBanStatus(
                userService.getUsers(null, 0, 2).get()
                        .filter(user -> user.getUsername().equals("username"))
                        .findFirst().get().getId(),
                banStatus);  // Banned 'username'

        // then
        var usersPage = userService.getUsers(null, 0, 1);
        assertEquals(1, usersPage.getTotalElements());
        assertTrue(usersPage.stream().anyMatch(user -> user.getUsername().equals("username")));
        assertEquals(banStatus, usersPage.stream().filter(user -> user.getUsername().equals("username"))
                .findAny().get().isBanned());
    }

    @Test
    void getUsersByUsername() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto(
                "mail@address.com", "one", "password"));
        authService.register(new RegisterRequestDto(
                "mail1@address.com", "another", "password"));

        // when
        var usersList = userService.getUsersByUsernameOrEmail("one");

        // then
        assertEquals(1, usersList.size());
        assertTrue(usersList.stream().anyMatch(user -> user.getUsername().equals("one")));
    }

    @Test
    void getUsersByEmail() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto(
                "mail@address.com", "one", "password"));
        authService.register(new RegisterRequestDto(
                "mail1@address.com", "another", "password"));

        // when
        var usersList = userService.getUsersByUsernameOrEmail("mail1@address.com");

        // then
        assertEquals(1, usersList.size());
        assertTrue(usersList.stream().anyMatch(user -> user.getUsername().equals("another")));
    }

}
