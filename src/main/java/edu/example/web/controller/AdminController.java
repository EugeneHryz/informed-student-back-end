package edu.example.web.controller;

import edu.example.dto.PageResponse;
import edu.example.dto.admin.UpdateUserRequestDto;
import edu.example.dto.user.UserRequestDto;
import edu.example.dto.user.UserResponseDto;
import edu.example.mapper.UserMapper;
import edu.example.model.Role;
import edu.example.service.AdminService;
import edu.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static java.util.Objects.isNull;


@RestController
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin panel functionality")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/commentsCleaningInterval")
    @Operation(description = "Change time interval at which old comments are deleted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Changes successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public void changeOldCommentsDeletionInterval(@RequestParam @Valid @Range(min = 1) int seconds) throws SchedulerException {
        adminService.changeOldCommentsDeletionInterval(seconds);
    }

    @PostMapping("/ageOfCommentsToDelete")
    @Operation(description = "Change the age of comments that are considered old and will be deleted automatically")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Changes successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public void changeAgeOfCommentsToDelete(@RequestParam @Valid @Range(min = 1) int days) throws SchedulerException {
        adminService.changeAgeOfCommentsToDelete(days);
    }

    @GetMapping("/users")
    @Operation(description = "Get users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public PageResponse<UserResponseDto> getUsers(@RequestBody UserRequestDto filter) {
        var result = userService.getUsers(filter.toPredicate(), filter.getPageNumber(), filter.getPageSize());

        return PageResponse.of(result, userMapper::toUserResponseDto);
    }

    @PostMapping("/users")
    @Operation(description = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public UserResponseDto updateUser(@RequestBody @Valid UpdateUserRequestDto dto) {
        Role role = isNull(dto.getRole()) ? null : Role.valueOf(dto.getRole());
        return userMapper.toUserResponseDto(userService.updateUser(dto.getUserId(), dto.getIsBanned(), role));
    }

    @GetMapping("/users/{username}")
    @Operation(description = "Get user by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public UserResponseDto getUserByUsername(@PathVariable String username) {
        return userMapper.toUserResponseDto(userService.getUserByUsername(username));
    }

    @GetMapping("/users/getByComment")
    @Operation(description = "Get user by comment id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public UserResponseDto getUserByCommentId(@RequestParam Long id) {
        return userMapper.toUserResponseDto(userService.getUserByCommentId(id));
    }
}
