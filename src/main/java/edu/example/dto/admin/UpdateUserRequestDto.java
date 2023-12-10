package edu.example.dto.admin;

import edu.example.validation.constraints.RoleConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserRequestDto {

    @NotNull
    private Long userId;

    @RoleConstraint
    private String role;

    private Boolean isBanned;
}
