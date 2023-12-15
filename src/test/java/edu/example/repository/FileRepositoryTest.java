package edu.example.repository;

import edu.example.TestContext;
import edu.example.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileRepositoryTest extends TestContext {

    @Autowired
    FileRepository fileRepository;

    @Autowired
    PostRepository postRepository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    @AfterEach
    public void clear() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "file", "post", "folder", "subject", "users");
    }

    @Test
    public void addFile() {
        // given
        var subject = subjectRepository.save(new Subject(0L, "physics", 3));
        var folder = folderRepository.save(new Folder(0L, subject, FolderType.TEST));
        var user = userRepository.save(new User(0L, "1234", "someone", "3534534", Role.USER, false));
        var post = postRepository.save(new Post(0L, folder,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post text", user, null, null));

        // when
        var newFile = fileRepository.save(new FileModel(0L, post, "original", "savedBy"));

        // then
        assertEquals("original", newFile.getOriginalName());
        assertEquals("savedBy", newFile.getSavedByName());
    }

}
