package edu.example.dto.userInfo;

import edu.example.model.Gender;
import lombok.Data;

import java.sql.Date;

@Data
public class UserInfoResponseDto {

    private String username;

    private Date dateOfBirth;

    private Gender gender;

    private Integer course;

    private String specialty;

    private String userImageFileName;

}
