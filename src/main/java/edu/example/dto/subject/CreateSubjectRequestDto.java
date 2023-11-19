package edu.example.dto.subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@AllArgsConstructor
public class CreateSubjectRequestDto {
    @NotBlank
    private String name;

    @NotNull
    @Range(max = Integer.MAX_VALUE)
    private Integer course;
}
