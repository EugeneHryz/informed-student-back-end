package edu.example.repository;

import edu.example.PostgresTestConfig;
import edu.example.model.Folder;
import edu.example.model.FolderType;
import edu.example.model.Post;
import edu.example.model.Subject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = PostgresTestConfig.Initializer.class)
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    FolderRepository folderRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @BeforeEach
    @AfterEach
    public void clear() {
        postRepository.deleteAll();
        folderRepository.deleteAll();
        subjectRepository.deleteAll();
    }

    @Test
    public void addPost() {
        // given
        var subject = subjectRepository.save(new Subject(0L, "physics", 3));
        var folder = folderRepository.save(new Folder(0L, subject, FolderType.TEST));

        // when
        var newPost = postRepository.save(new Post(0L, folder,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post text"));

        // then
        assertEquals(folder.getId(), newPost.getFolder().getId());
        assertEquals(new Timestamp(System.currentTimeMillis()).getTime(), newPost.getCreatedAt().getTime(), 10);
        assertEquals("Post text", newPost.getText());
    }

}
