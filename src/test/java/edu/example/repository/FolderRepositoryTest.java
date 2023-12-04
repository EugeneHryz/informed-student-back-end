package edu.example.repository;

import edu.example.model.Folder;
import edu.example.model.FolderType;
import edu.example.model.Subject;
import edu.example.config.MinioTestConfig;
import edu.example.config.PostgresTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ContextConfiguration(initializers = {MinioTestConfig.Initializer.class, PostgresTestConfig.Initializer.class})
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

    @Test
    public void getFoldersBySubject() {
        // given
        var subjectPhysics = subjectRepository.save(new Subject(0L, "physics", 3));
        var physicsTests = folderRepository.save(new Folder(0L, subjectPhysics, FolderType.TEST));
        var physicsNotes = folderRepository.save(new Folder(0L, subjectPhysics, FolderType.NOTES));

        var subjectMaths = subjectRepository.save(new Subject(0L, "maths", 5));
        var mathsLiterature = folderRepository.save(new Folder(0L, subjectMaths, FolderType.LITERATURE));

        // when
        var result = folderRepository.getFoldersBySubject(subjectPhysics);

        // then
        assertEquals(2, result.size());
        var resArr = result.toArray(new Folder[0]);
        assertTrue((resArr[0].getId() == physicsTests.getId() && resArr[1].getId() == physicsNotes.getId())
            || (resArr[1].getId() == physicsTests.getId() && resArr[0].getId() == physicsNotes.getId()));
    }

}
