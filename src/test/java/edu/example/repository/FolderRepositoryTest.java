package edu.example.repository;

import edu.example.PostgresTestConfig;
import edu.example.model.Folder;
import edu.example.model.FolderType;
import edu.example.model.Subject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = PostgresTestConfig.Initializer.class)
public class FolderRepositoryTest {

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @BeforeEach
    @AfterEach
    public void clear() {
        folderRepository.deleteAll();
        subjectRepository.deleteAll();
    }

    @Test
    public void addFolder() {
        // given
        var subject = subjectRepository.save(new Subject(0L, "physics", 3));

        // when
        var newFolder = folderRepository.save(new Folder(0L, subject, FolderType.TEST));

        // then
        assertEquals(FolderType.TEST, newFolder.getType());
        assertEquals(subject.getId(), newFolder.getSubject().getId());
    }

}
