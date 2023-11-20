package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.Comment;
import edu.example.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public List<Comment> getCommentsByPost(long postId) {
        return commentRepository.findByPostIdOrderByCreatedAt(postId);
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    public Comment createComment(Long postId, String text) {
        var comment = new Comment();
        comment.setPost(postService.getPost(postId));
        comment.setText(text);
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, Long postId, String text) {
        var comment = new Comment();
        comment.setId(id);
        comment.setPost(postService.getPost(postId));
        comment.setText(text);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        commentRepository.deleteById(id);
    }
}