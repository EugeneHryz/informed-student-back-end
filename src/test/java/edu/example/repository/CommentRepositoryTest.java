package edu.example.repository;

import edu.example.model.*;
import edu.example.config.MinioTestConfig;
import edu.example.config.PostgresTestConfig;
import io.minio.MinioClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(initializers = {MinioTestConfig.Initializer.class, PostgresTestConfig.Initializer.class})
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostRepository postRepository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    @AfterEach
    public void clear() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        folderRepository.deleteAll();
        subjectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void addComment() {
        // given
        var subject = subjectRepository.save(new Subject(0L, "physics", 3));
        var folder = folderRepository.save(new Folder(0L, subject, FolderType.TEST));
        var user = userRepository.save(new User(0L, "1234", "someone", "3534534", Role.USER, false));
        var post = postRepository.save(new Post(0L, folder,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post text", user, null, null));

        // when
        var newComment = commentRepository.save(new Comment(0L, post,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Comment text", user, false));

        // then
        assertEquals(post.getId(), newComment.getPost().getId());
        assertEquals(new Timestamp(System.currentTimeMillis()).getTime(), newComment.getCreatedAt().getTime(), 10);
        assertEquals("Comment text", newComment.getText());
    }

    @Test
    public void getCommentByPost() {
        // given
        var subject = subjectRepository.save(new Subject(0L, "physics", 3));
        var folder = folderRepository.save(new Folder(0L, subject, FolderType.TEST));
        var user = userRepository.save(new User(0L, "1234", "someone", "3534534", Role.USER, false));

        var post1 = postRepository.save(new Post(0L, folder,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post1 text", user, null, null));
        var comment1Post1 = commentRepository.save(new Comment(0L, post1,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Comment1 text", user, false));
        var comment2Post1 = commentRepository.save(new Comment(0L, post1,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Comment2 text", user, false));

        var post2 = postRepository.save(new Post(0L, folder,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post2 text", user, null, null));
        var comment1Post2 = commentRepository.save(new Comment(0L, post2,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Comment3 text", user, false));

        // when
        var result = commentRepository.findCommentsByPostOrderByCreatedAt(post1);

        // then
        assertEquals(2, result.size());
        assertEquals(comment1Post1.getId(), result.get(0).getId());
        assertEquals(comment2Post1.getId(), result.get(1).getId());
    }

}
