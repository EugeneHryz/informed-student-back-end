package edu.example.dto.subject;

import edu.example.dto.PageDto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;


@Data
@EqualsAndHashCode(callSuper = true)
public class SubjectByCourseRequestDto extends PageDto {
    @NotNull
    @Range(max = Integer.MAX_VALUE)
    private Integer course;
}
