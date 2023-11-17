package edu.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subject")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subject_seq")
    @SequenceGenerator(name = "subject_seq", sequenceName = "subject_seq", allocationSize = 1)
    private Long id;

    @Column
    private String name;

    @Column
    private Integer course;

}
