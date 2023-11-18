package edu.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "folder")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "folder_seq")
    @SequenceGenerator(name = "folder_seq", sequenceName = "folder_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column
    @Enumerated(EnumType.STRING)
    private FolderType type;

}
