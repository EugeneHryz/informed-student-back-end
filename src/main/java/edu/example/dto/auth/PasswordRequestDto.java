package edu.example.dto.auth;

import edu.example.validation.constraints.PasswordComplexityConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordRequestDto {

    @PasswordComplexityConstraint
    String password;

}
