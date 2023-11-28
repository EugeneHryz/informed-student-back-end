package edu.example.dto.auth;

import edu.example.validation.constraints.PasswordComplexityConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequestDto {

    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email address invalid")
    private String email;

    @NotEmpty
    private String username;

    @NotEmpty
    @PasswordComplexityConstraint
    private String password;
}
