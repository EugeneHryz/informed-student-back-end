package edu.example.web.config;

import edu.example.model.Role;
import edu.example.model.User;
import edu.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import static java.util.Objects.isNull;

@Configuration
@RequiredArgsConstructor
public class AdminUserConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.username}")
    private String username;
    @Value("${admin.password}")
    private String password;

    @EventListener(ApplicationReadyEvent.class)
    public void processAdminUser() {
        if (isNull(username) || isNull(password)) {
            throw new RuntimeException("Admin user incorrect credentials (null)");
        }
        var userCheckUsername = userRepository.findByUsername(username);
        if (userCheckUsername.isPresent() && !userCheckUsername.get().getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Provided admin username already occupied by non-admin");
        }

        var adminUsers = userRepository.findByRole(Role.ADMIN);
        if (adminUsers.size() > 1) {
            throw new RuntimeException("Unexpected: there is more than one administrator");
        }
        User admin = adminUsers.isEmpty() ? new User() : adminUsers.get(0);
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }
}
