package edu.example.web.controller;

import edu.example.dto.PageResponse;
import edu.example.dto.admin.UpdateUserBanRequestDto;
import edu.example.dto.user.UserResponseDto;
import edu.example.mapper.UserMapper;
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

import java.util.List;

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
    @Operation(description = "Get users by ban status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public PageResponse<UserResponseDto> getUsers(@RequestParam(required = false) Boolean isBanned,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "5") int size) {
        var result = userService.getUsers(isBanned, page, size);

        var response = new PageResponse<UserResponseDto>();
        response.setPageSize(result.getSize());
        response.setPageNumber(result.getNumber());
        response.setTotalPages(result.getTotalPages());
        response.setTotalSize(result.getTotalElements());
        response.setContent(result.getContent().stream().map(userMapper::toUserResponseDto).toList());

        return response;
    }

    @DeleteMapping("/users")
    @Operation(description = "Hard delete user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public void deleteUser(@RequestParam Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/users/search")
    @Operation(description = "Search users by their email or username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public List<UserResponseDto> getUsersByUsernameOrEmail(@RequestParam String search) {
        var result = userService.getUsersByUsernameOrEmail(search);
        return result.stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

    @PostMapping("/users/ban")
    @Operation(description = "Update user ban status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorized")
    })
    public UserResponseDto updateUserBanStatus(@RequestBody @Valid UpdateUserBanRequestDto dto) {
        return userMapper.toUserResponseDto(userService.updateUserBanStatus(dto.getUserId(), dto.isBanned()));
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
