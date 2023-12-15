package edu.example.service;

import edu.example.TestContext;
import edu.example.dto.auth.RegisterRequestDto;
import edu.example.dto.user.UserRequestDto;
import edu.example.exception.EntityNotFoundException;
import edu.example.exception.UnprocessableEntityException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest extends TestContext {

    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    void clear() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "token", "users");
    }

    @Test
    void getAllUsers() throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto(
                "mail@address.com", "username", "password"));
        authService.register(new RegisterRequestDto(
                "mail1@address.com", "username1", "password"));
        userService.updateUser(
                userService.getUsers(new UserRequestDto().toPredicate(), 0, 2).get()
                        .filter(user -> user.getUsername().equals("username"))
                        .findAny().get().getId(),
                true, null);  // Banned 'username'

        // when
        var usersPage = userService.getUsers(new UserRequestDto().toPredicate(), 0, 2);

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
        userService.updateUser(
                userService.getUsers(new UserRequestDto().toPredicate(), 0, 2).get()
                        .filter(user -> user.getUsername().equals("username"))
                        .findFirst().get().getId(),
                true, null);  // Banned 'username'

        // when
        var dto = new UserRequestDto();
        dto.setIsBanned(false);
        var usersPage = userService.getUsers(dto.toPredicate(), 0, 2);

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
        userService.deleteUser(userService.getUsers(new UserRequestDto().toPredicate(), 0, 1)
                .get().toList().get(0).getId());

        // then
        var usersPage = userService.getUsers(new UserRequestDto().toPredicate(), 0, 1);
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
                userService.updateUser(0L, true, null));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void updateUserBanStatus(boolean banStatus) throws UnprocessableEntityException {
        // given
        authService.register(new RegisterRequestDto(
                "mail@address.com", "username", "password"));

        // when
        userService.updateUser(
                userService.getUsers(new UserRequestDto().toPredicate(), 0, 2).get()
                        .filter(user -> user.getUsername().equals("username"))
                        .findFirst().get().getId(),
                banStatus, null);  // Banned 'username'

        // then
        var usersPage = userService.getUsers(new UserRequestDto().toPredicate(), 0, 1);
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
        var dto = new UserRequestDto();
        dto.setSearchTerm("one");
        var usersList = userService.getUsers(dto.toPredicate(), 0, 1).getContent();

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
        var dto = new UserRequestDto();
        dto.setSearchTerm("mail1@address.com");
        var usersList = userService.getUsers(dto.toPredicate(), 0, 1).getContent();

        // then
        assertEquals(1, usersList.size());
        assertTrue(usersList.stream().anyMatch(user -> user.getUsername().equals("another")));
    }

}
