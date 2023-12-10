package edu.example.dto.auth;

import edu.example.validation.constraints.PasswordComplexityConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    @Email
    @Schema(example = "address@example.com")
    private String email;

    @NotEmpty
    private String username;

    @NotEmpty
    @PasswordComplexityConstraint
    private String password;
}
