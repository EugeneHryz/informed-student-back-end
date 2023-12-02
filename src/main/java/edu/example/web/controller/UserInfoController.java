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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @Operation(summary = "Saves or updates user's info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saved/updated successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Parsing / validation error")
    })
    public UserInfoResponseDto createOrUpdateUserInfo(@RequestBody @Valid UserInfoCreateUpdateDto userInfo) {
        return userInfoMapper.toUserInfoResponseDto(
                userInfoService.createOrUpdateUserInfo(
                        userInfoMapper.toUserInfo(userInfo)));
    }

    @GetMapping
    @Operation(summary = "Retrieve user's info")
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

    @PostMapping(path = "/setImage", consumes = {MediaType.IMAGE_JPEG_VALUE})
    @Operation(summary = "Set user's image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uploaded successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "415", description = "File type not supported")
    })
    public void setUserImage(@RequestParam String username,
                             @RequestPart("image") MultipartFile image) {
        userInfoService.setUserImage(username, image);
    }

    @GetMapping("/getImage")
    @Operation(summary = "Get user's image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Insufficient rights / unauthorised"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public String getUserImage(String username) {
        return userInfoService.getUserImageName(username);
    }

}
