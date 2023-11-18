package edu.example.repository;

import edu.example.MinioTestConfig;
import edu.example.PostgresTestConfig;
import edu.example.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = {MinioTestConfig.Initializer.class, PostgresTestConfig.Initializer.class})
public class FileRepositoryTest {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    PostRepository postRepository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @BeforeEach
    @AfterEach
    public void clear() {
        fileRepository.deleteAll();
        postRepository.deleteAll();
        folderRepository.deleteAll();
        subjectRepository.deleteAll();
    }

    @Test
    public void addFile() {
        // given
        var subject = subjectRepository.save(new Subject(0L, "physics", 3));
        var folder = folderRepository.save(new Folder(0L, subject, FolderType.TEST));
        var post = postRepository.save(new Post(0L, folder,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post text", null));

        // when
        var newFile = fileRepository.save(new FileModel(0L, "original", "savedBy"));

        // then
        assertEquals("original", newFile.getOriginalName());
        assertEquals("savedBy", newFile.getSavedByName());
    }

}
