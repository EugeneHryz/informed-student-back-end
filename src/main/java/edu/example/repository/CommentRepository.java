package edu.example.repository;

import edu.example.model.Comment;
import edu.example.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentsByPostOrderByCreatedAt(Post post);

    List<Comment> findByPostIdOrderByCreatedAt(Long postId);
}
