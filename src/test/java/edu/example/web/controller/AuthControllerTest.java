package edu.example.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.example.dto.auth.AuthUserDto;
import edu.example.dto.auth.LoginRequestDto;
import edu.example.dto.auth.RegisterRequestDto;
import edu.example.repository.TokenRepository;
import edu.example.repository.UserRepository;
import edu.example.service.AuthService;
import edu.example.config.PostgresTestConfig;
import edu.example.web.security.SecurityConstants;
import io.minio.MinioClient;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(initializers = {PostgresTestConfig.Initializer.class})
class AuthControllerTest {

    static final String EMAIL = "user@email.com";
    static final String PASSWORD = "1234qwerQkL*";
    static final String USERNAME = "username";

    @TestConfiguration
    public static class Configuration {

        @Bean
        public MinioClient minioClient(){
            return Mockito.mock(MinioClient.class);
        }

        @Bean
        public Scheduler scheduler() {
            return Mockito.mock(Scheduler.class);
        }
    }

    @Autowired
    MinioClient minioClient;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    AuthService authService;

    @BeforeEach
    public void clear() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registerNewUser() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto(EMAIL, USERNAME, PASSWORD);
        String registerRequestBody = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestBody))
                .andExpect(status().isOk())
                .andExpectAll(jsonPath("username").isString(),
                        jsonPath("roles").isArray());
    }

    @Test
    void registerWithExistingUsername() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto(EMAIL, USERNAME, PASSWORD);
        authService.register(registerRequest);

        RegisterRequestDto anotherRegisterRequest = new RegisterRequestDto("abc@w.com", USERNAME, "7890qwerT");
        String registerRequestBody = objectMapper.writeValueAsString(anotherRegisterRequest);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerRequestBody))
                .andExpect(status().isConflict());
    }

    @Test
    void login() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto(EMAIL, USERNAME, PASSWORD);
        authService.register(registerRequest);

        LoginRequestDto loginRequest = new LoginRequestDto(USERNAME, PASSWORD);
        String loginRequestBody = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isOk())
                .andExpectAll(jsonPath("username").isString(),
                        jsonPath("roles").isArray());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void loginWithInvalidCredentials(boolean invalidUsername) throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto(EMAIL, USERNAME, PASSWORD);
        authService.register(registerRequest);

        LoginRequestDto loginRequest = invalidUsername ? new LoginRequestDto("123ggggg", PASSWORD)
                : new LoginRequestDto(USERNAME, "lllayUUUddd");
        String loginRequestBody = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestBody))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAuthenticatedUserInfo() throws Exception {
        RegisterRequestDto registerRequest = new RegisterRequestDto(EMAIL, USERNAME, PASSWORD);
        AuthUserDto registerResponse = authService.register(registerRequest);

        mockMvc.perform(get("/auth/user")
                        .cookie(createCookieWithToken(registerResponse.getToken())))
                .andExpect(status().isOk())
                .andExpectAll(jsonPath("username").isString(),
                        jsonPath("roles").isArray());
    }

    private static Cookie createCookieWithToken(String token) {
        Cookie cookie = new Cookie(SecurityConstants.JWT_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    @Test
    void getAuthenticatedUserInfoWithoutToken() throws Exception {
        mockMvc.perform(get("/auth/user"))
                .andExpect(status().isForbidden());
    }
}
