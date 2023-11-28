package edu.example.web.controller;

import edu.example.dto.auth.AuthUserDto;
import edu.example.dto.auth.LoginRequestDto;
import edu.example.dto.auth.RegisterRequestDto;
import edu.example.exception.UnprocessableEntityException;
import edu.example.service.AuthService;
import edu.example.web.security.SecurityConstants;
import edu.example.web.security.UserInfoDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @RequestMapping("/user")
    public ResponseEntity<AuthUserDto> user(@AuthenticationPrincipal UserInfoDetails userDetails) {
        AuthUserDto user = new AuthUserDto();
        user.setUsername(userDetails.getUsername());
        user.setRoles(userDetails.getRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet()));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthUserDto> register(@RequestBody @Valid RegisterRequestDto requestDto,
                                      HttpServletResponse response)
            throws UnprocessableEntityException {
        AuthUserDto authUser = authService.register(requestDto);
        addTokenCookieToResponse(response, authUser.getToken());
        return ResponseEntity.ok(authUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthUserDto> login(@RequestBody LoginRequestDto requestDto,
                                   HttpServletResponse response) {
        AuthUserDto authUser = authService.login(requestDto);
        addTokenCookieToResponse(response, authUser.getToken());
        return ResponseEntity.ok(authUser);
    }

    private void addTokenCookieToResponse(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(SecurityConstants.JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
