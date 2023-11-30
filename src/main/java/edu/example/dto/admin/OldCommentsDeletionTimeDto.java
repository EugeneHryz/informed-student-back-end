package edu.example.dto.admin;

import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Getter
public class OldCommentsDeletionTimeDto {

    @Range(min = 0, max = 24, message = "Hours value should be [0; 24]")
    private int hours;

    @Range(min = 0, max = 60, message = "Minutes value should be [0; 60]")
    private int minutes;

    @Range(min = 0, max = 60, message = "Seconds value should be [0; 60]")
    private int seconds;

}
