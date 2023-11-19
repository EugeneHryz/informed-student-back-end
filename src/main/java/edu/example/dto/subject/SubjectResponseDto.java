package edu.example.dto.subject;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubjectResponseDto {
    private Long id;

    private String name;

    private Integer course;
}
