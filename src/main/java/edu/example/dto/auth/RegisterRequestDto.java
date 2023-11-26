package edu.example.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDto {

    private String email;

    @NotEmpty
    private String username;

    // todo: add validation rules for a password
    @NotEmpty
    private String password;
}
