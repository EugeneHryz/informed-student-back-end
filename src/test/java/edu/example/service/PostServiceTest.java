package edu.example.service;

import edu.example.TestContext;
import edu.example.dto.auth.RegisterRequestDto;
import edu.example.exception.EntityNotFoundException;
import edu.example.exception.UnprocessableEntityException;
import edu.example.model.FolderType;
import edu.example.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PostServiceTest extends TestContext {

    @Autowired
    PostService postService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    FolderService folderService;
    @Autowired
    UserService userService;
    @Autowired
    AuthService authService;

    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    FolderRepository folderRepository;
    @Autowired
    PostRepository postRepository;

    @BeforeEach
    @AfterEach
    void clear() {
        postRepository.deleteAll();
        folderRepository.deleteAll();
        subjectRepository.deleteAll();

        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createPost() throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        // when
        var post = postService.createPost(folder.getId(), "Text", user);

        // then
        assertEquals("Text", post.getText());
        assertEquals(folder.getId(), post.getFolder().getId());
        assertEquals("username", post.getUser().getUsername());
        assertEquals(Timestamp.valueOf(LocalDateTime.now()).getTime(),
                post.getCreatedAt().getTime(), 100);
    }

    @Test
    void getPost() throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var post_id = postService.createPost(folder.getId(), "Text", user).getId();

        // when
        var post = postService.getPost(post_id);

        // then
        assertEquals("Text", post.getText());
        assertEquals(folder.getId(), post.getFolder().getId());
        assertEquals("username", post.getUser().getUsername());
        assertEquals(Timestamp.valueOf(LocalDateTime.now()).getTime(),
                post.getCreatedAt().getTime(), 100);
    }

    @Test
    void getPostNotFound() {
        // when
        assertThrows(EntityNotFoundException.class, () ->
                postService.deletePost(0L));
    }

    @Test
    void deletePost() throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var post_id = postService.createPost(folder.getId(), "Text", user).getId();

        // when
        postService.deletePost(post_id);

        // then
        assertThrows(EntityNotFoundException.class,
                () -> postService.getPost(post_id));
    }

    @Test
    void deletePostNotFound() {
        // when
        assertThrows(EntityNotFoundException.class, () ->
                postService.deletePost(0L));
    }

    @Test
    void updatePost() throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var post_id = postService.createPost(folder.getId(), "Text", user).getId();

        // when
        postService.updatePost(post_id, folder.getId(), "New text");

        // then
        var post = postService.getPost(post_id);
        assertEquals("New text", post.getText());
        assertEquals(folder.getId(), post.getFolder().getId());
        assertEquals("username", post.getUser().getUsername());
        assertEquals(Timestamp.valueOf(LocalDateTime.now()).getTime(),
                post.getCreatedAt().getTime(), 100);
    }

    @Test
    void updatePostNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                postService.updatePost(0L, 0L, ""));
    }

    @Test
    void getPostsByFolder() throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);
        var folder1 = folderService.createFolder(subject.getId(), FolderType.LITERATURE);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var post_id = postService.createPost(folder.getId(), "Text", user).getId();

        // when
        var page = postService.getPostsByFolder(0, 1, folder.getId());

        // then
        assertEquals(1, page.getTotalElements());

        var post = page.get().findFirst().get();
        assertEquals("Text", post.getText());
        assertEquals(folder.getId(), post.getFolder().getId());
        assertEquals("username", post.getUser().getUsername());
        assertEquals(Timestamp.valueOf(LocalDateTime.now()).getTime(),
                post.getCreatedAt().getTime(), 100);
    }

}
