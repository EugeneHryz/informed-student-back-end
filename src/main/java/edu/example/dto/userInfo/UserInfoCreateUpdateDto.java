package edu.example.dto.userInfo;

import edu.example.validation.constraints.DateConstraint;
import edu.example.validation.constraints.GenderConstraint;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class UserInfoCreateUpdateDto {

    private String username;

    @DateConstraint
    @Schema(description = "User's date of birth in format YYYY-MM-DD", example = "2000-01-01")
    private String dateOfBirth;

    @GenderConstraint
    @Schema(description = "User's gender (MALE/FEMALE)", example = "MALE")
    private String gender;

    @Range(min = 1, max = 6, message = "Course should be in range [1;6]")
    @Schema(description = "User's course [1;4] - bachelor, [5;6] - magistrate", example = "1")
    private Integer course;

    @Size(max = 100)
    private String specialty;

    @Schema(description = "Name, user's image is saved by in storage")
    private String userImageFileName;

}
