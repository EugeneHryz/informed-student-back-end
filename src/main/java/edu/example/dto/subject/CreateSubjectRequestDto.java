package edu.example.dto.subject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
public class CreateSubjectRequestDto {
    @NotBlank
    @Schema(description = "Subject's name")
    private String name;

    @NotNull
    @Range(min = 1, max = 6)
    @Schema(description = "Course number (bachelor: 1-4, master: 5-6)")
    private Integer course;
}
