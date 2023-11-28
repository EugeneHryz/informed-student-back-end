package edu.example.service;

import edu.example.dto.auth.AuthUserDto;
import edu.example.dto.auth.LoginRequestDto;
import edu.example.dto.auth.RegisterRequestDto;
import edu.example.exception.UnprocessableEntityException;
import edu.example.model.Role;
import edu.example.model.User;
import edu.example.repository.UserRepository;
import edu.example.web.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final TokenService tokenService;

    @Transactional
    public AuthUserDto register(RegisterRequestDto registerRequest) throws UnprocessableEntityException {
        Optional<User> existingUser = userRepository.findByUsername(registerRequest.getUsername());
        if (existingUser.isPresent()) {
            throw new UnprocessableEntityException("User with that username already exists");
        }
        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        String generatedToken = jwtService.generateFromUser(user);
        tokenService.saveNewToken(generatedToken, user);
        return new AuthUserDto(generatedToken, user.getUsername(), user.getRole().getRoles()
                .stream().map(Enum::name)
                .collect(Collectors.toSet()));
    }

    @Transactional
    public AuthUserDto login(LoginRequestDto loginRequest) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user cannot be null"));

        tokenService.deactivateUserTokens(user.getId());
        String generatedToken = jwtService.generateFromUser(user);
        tokenService.saveNewToken(generatedToken, user);
        return new AuthUserDto(generatedToken, user.getUsername(), user.getRole().getRoles()
                .stream().map(Enum::name)
                .collect(Collectors.toSet()));
    }
}
