package edu.example.repository;

import edu.example.MinioTestConfig;
import edu.example.PostgresTestConfig;
import edu.example.model.*;
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
@ContextConfiguration(initializers = {MinioTestConfig.Initializer.class, PostgresTestConfig.Initializer.class})
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
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post text", null));

        // then
        assertEquals(folder.getId(), newPost.getFolder().getId());
        assertEquals(new Timestamp(System.currentTimeMillis()).getTime(), newPost.getCreatedAt().getTime(), 10);
        assertEquals("Post text", newPost.getText());
    }

    @Test
    public void getPostByFolder() {
        // given
        var subject = subjectRepository.save(new Subject(0L, "physics", 3));

        var folderTest = folderRepository.save(new Folder(0L, subject, FolderType.TEST));
        var postTest1 = postRepository.save(new Post(0L, folderTest,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post1 text", null));
        var postTest2 = postRepository.save(new Post(0L, folderTest,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post2 text", null));

        var folderNotes = folderRepository.save(new Folder(0L, subject, FolderType.NOTES));
        var postNotes3 = postRepository.save(new Post(0L, folderNotes,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post3 text", null));

        // when
        var result = postRepository.getPostsByFolderOrderByCreatedAt(folderTest);

        // then
        assertEquals(2, result.size());
        assertEquals(postTest1.getId(), result.get(0).getId());
        assertEquals(postTest2.getId(), result.get(1).getId());
    }

    @Test
    public void addPostWithFile() {
        // given
        var subject = subjectRepository.save(new Subject(0L, "physics", 3));
        var folder = folderRepository.save(new Folder(0L, subject, FolderType.TEST));

        // when
        var newPost = postRepository.save(new Post(0L, folder,
                Timestamp.valueOf("1970-01-01 00:00:00"), "Post text",
                new FileModel(0L, "origin", "savedBy")));

        // then
        assertEquals(folder.getId(), newPost.getFolder().getId());
        assertEquals(new Timestamp(System.currentTimeMillis()).getTime(), newPost.getCreatedAt().getTime(), 10);
        assertEquals("Post text", newPost.getText());

        assertEquals("origin", newPost.getFile().getOriginalName());
        assertEquals("savedBy", newPost.getFile().getSavedByName());
    }

}
