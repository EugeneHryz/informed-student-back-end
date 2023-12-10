package edu.example.service;

import edu.example.TestContext;
import edu.example.dto.auth.RegisterRequestDto;
import edu.example.dto.comment.CreateCommentRequestDto;
import edu.example.exception.EntityNotFoundException;
import edu.example.exception.UnprocessableEntityException;
import edu.example.model.FolderType;
import edu.example.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceTest extends TestContext {

    @Autowired
    CommentService commentService;
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
    @Autowired
    CommentRepository commentRepository;

    @BeforeEach
    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        postRepository.deleteAll();
        folderRepository.deleteAll();
        subjectRepository.deleteAll();

        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void createComment(boolean isAnonymous) throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var post = postService.createPost(folder.getId(), "Text", user);

        // when
        var comment = commentService.createComment(
                new CreateCommentRequestDto(post.getId(), "Comment text", isAnonymous), user);

        // then
        assertEquals(post.getId(), comment.getPost().getId());
        assertEquals("Comment text", comment.getText());
        assertEquals(user.getUsername(), comment.getUser().getUsername());
        assertEquals(Timestamp.valueOf(LocalDateTime.now()).getTime(),
                comment.getCreatedAt().getTime(), 100);
        assertEquals(isAnonymous, comment.isAnonymous());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getComment(boolean isAnonymous) throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var post = postService.createPost(folder.getId(), "Text", user);

        var comment_id = commentService.createComment(
                new CreateCommentRequestDto(post.getId(), "Comment text", isAnonymous), user).getId();

        // when
        var comment = commentService.getComment(comment_id);

        // then
        assertEquals(post.getId(), comment.getPost().getId());
        assertEquals("Comment text", comment.getText());
        assertEquals(user.getUsername(), comment.getUser().getUsername());
        assertEquals(Timestamp.valueOf(LocalDateTime.now()).getTime(),
                comment.getCreatedAt().getTime(), 100);
        assertEquals(isAnonymous, comment.isAnonymous());
    }

    @Test
    void getCommentNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                commentService.getComment(0L));
    }

    @Test
    void deleteComment() throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var post = postService.createPost(folder.getId(), "Text", user);

        var comment_id = commentService.createComment(
                new CreateCommentRequestDto(post.getId(), "Comment text", false), user).getId();

        // when
        commentService.deleteComment(comment_id);

        // then
        assertThrows(EntityNotFoundException.class, () ->
                commentService.getComment(comment_id));
    }

    @Test
    void deleteCommentNotFound() {
        // when
        assertThrows(EntityNotFoundException.class, () ->
                commentService.deleteComment(0L));
    }

    @Test
    void deleteCommentsOlderThenNoDelete() throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var post = postService.createPost(folder.getId(), "Text", user);

        var comment1 = commentService.createComment(
                new CreateCommentRequestDto(post.getId(), "Comment1 text", false), user);

        // when
        commentService.deleteCommentsOlderThen(0, 0, 1);

        // then
        assertEquals(comment1.getId(), commentService.getComment(comment1.getId()).getId());
    }

    @Test
    void updateComment() throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var post = postService.createPost(folder.getId(), "Text", user);

        var comment_id = commentService.createComment(
                new CreateCommentRequestDto(post.getId(), "Comment text", false), user).getId();

        // when
        var comment = commentService.updateComment(comment_id, "New text");

        // then
        assertEquals(post.getId(), comment.getPost().getId());
        assertEquals("New text", comment.getText());
        assertEquals(user.getUsername(), comment.getUser().getUsername());
        assertEquals(Timestamp.valueOf(LocalDateTime.now()).getTime(),
                comment.getCreatedAt().getTime(), 100);
        assertFalse(comment.isAnonymous());
    }

    @Test
    void updateCommentNotFound() {
        assertThrows(EntityNotFoundException.class, () ->
                commentService.updateComment(0L, ""));
    }

    @Test
    void getCommentsByPost() throws UnprocessableEntityException {
        // given
        var subject = subjectService.createSubject("Physics", 1);
        var folder = folderService.createFolder(subject.getId(), FolderType.NOTES);

        authService.register(new RegisterRequestDto("mail@address.com", "username", "password"));
        var user = userService.getUsers(false, 0, 1).get().findFirst().get();

        var post = postService.createPost(folder.getId(), "Text", user);

        commentService.createComment(
                new CreateCommentRequestDto(post.getId(), "Comment text", false), user);

        // when
        var page = commentService.getCommentsByPost(0, 1, post.getId());

        // then
        assertEquals(1, page.getNumberOfElements());
        var comment = page.get().findFirst().get();
        assertEquals(post.getId(), comment.getPost().getId());
        assertEquals("Comment text", comment.getText());
        assertEquals(user.getUsername(), comment.getUser().getUsername());
        assertEquals(Timestamp.valueOf(LocalDateTime.now()).getTime(),
                comment.getCreatedAt().getTime(), 100);
        assertFalse(comment.isAnonymous());
    }

}
