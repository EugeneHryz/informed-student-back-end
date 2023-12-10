package edu.example.dto.userInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoCreateUpdateDto {

    // todo: validation is removed because we need to allow null values

    @Schema(description = "User's date of birth in format YYYY-MM-DD", example = "2000-01-01")
    private String dateOfBirth;

    @Schema(description = "User's gender (MALE/FEMALE)", example = "MALE")
    private String gender;

    @Schema(description = "User's course [1;4] - bachelor, [5;6] - magistrate", example = "1")
    private Integer course;

    private String specialty;

    @Schema(description = "Name, user's image is saved by in storage")
    private String userImageFileName;
}
