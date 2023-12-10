package edu.example.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateUserBanRequestDto {

    @NotNull
    private Long userId;

    private boolean isBanned;
}
