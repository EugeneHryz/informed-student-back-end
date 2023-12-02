package edu.example.web.controller;

import edu.example.dto.userInfo.UserInfoCreateUpdateDto;
import edu.example.dto.userInfo.UserInfoResponseDto;
import edu.example.mapper.UserInfoMapper;
import edu.example.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userInfo")
@Tag(name = "UserInfo", description = "Work with user's info")
public class UserInfoController {

    private final UserInfoService userInfoService;

    private final UserInfoMapper userInfoMapper;

    public UserInfoController(UserInfoService userInfoService, UserInfoMapper userInfoMapper) {
        this.userInfoService = userInfoService;
        this.userInfoMapper = userInfoMapper;
    }

    @PostMapping
    @Operation(description = "Save user's info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Parsing / validation error")
    })
    public UserInfoResponseDto addUserInfo(@RequestBody @Valid UserInfoCreateUpdateDto userInfo) {
        return userInfoMapper.toUserInfoResponseDto(
                userInfoService.createUserInfo(
                        userInfoMapper.toUserInfo(userInfo)));
    }

    @GetMapping
    @Operation(description = "Retrieve user's info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Parsing / validation error")
    })
    public UserInfoResponseDto getUserInfo(String username) {
        return userInfoMapper.toUserInfoResponseDto(
                userInfoService.getUserInfo(username));
    }

    @PutMapping
    @Operation(description = "Update user's info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Parsing / validation error")
    })
    public UserInfoResponseDto updateUserInfo(@RequestBody @Valid UserInfoCreateUpdateDto userInfo) {
        return userInfoMapper.toUserInfoResponseDto(
                userInfoService.updateUserInfo(
                        userInfoMapper.toUserInfo(userInfo)));
    }

}
