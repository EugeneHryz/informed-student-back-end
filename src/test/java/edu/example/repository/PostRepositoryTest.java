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
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PostRepositoryTest extends TestContext {

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
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "post", "folder", "subject", "users");
    }

    @Test
    public void addPost() {
        // given
        var subject = subjectRepository.save(new Subject(0L, "physics", 3));
        var folder = folderRepository.save(new Folder(0L, subject, FolderType.TEST));
        var user = userRepository.save(new User(0L, "1234", "someone", "3534534", Role.USER, false));

        // when
        Instant now = Instant.now();
        var newPost = postRepository.save(new Post(0L, folder,
                Timestamp.from(now), "Post text", user, null, null));

        // then
        assertEquals(folder.getId(), newPost.getFolder().getId());
        assertEquals(Timestamp.from(now).getTime(), newPost.getCreatedAt().getTime(), 10);
        assertEquals("Post text", newPost.getText());
    }

    @Test
    public void getPostByFolder() {
        // given
        var subject = subjectRepository.save(new Subject(0L, "physics", 3));
        var user = userRepository.save(new User(0L, "1234", "someone", "3534534", Role.USER, false));

        var folderTest = folderRepository.save(new Folder(0L, subject, FolderType.TEST));
        var postTest1 = postRepository.save(new Post(0L, folderTest,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post1 text", user, null, null));
        var postTest2 = postRepository.save(new Post(0L, folderTest,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post2 text", user, null, null));

        var folderNotes = folderRepository.save(new Folder(0L, subject, FolderType.NOTES));
        var postNotes3 = postRepository.save(new Post(0L, folderNotes,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post3 text", user, null, null));

        // when
        var result = postRepository.getPostsByFolderOrderByCreatedAt(folderTest);

        // then
        assertEquals(2, result.size());
        assertEquals(postTest1.getId(), result.get(0).getId());
        assertEquals(postTest2.getId(), result.get(1).getId());
    }

}
