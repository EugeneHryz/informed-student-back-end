package edu.example.dto.user;

import edu.example.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private String username;
    private Role role;
    private boolean isBanned;
}
