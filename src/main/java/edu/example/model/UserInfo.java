package edu.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "user_info")
@Getter
@Setter
public class UserInfo {

    @Id
    private String username;

    @Column(name = "user_image_name")
    private String userImage;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private Integer course;

    @Column
    private String specialty;

}
