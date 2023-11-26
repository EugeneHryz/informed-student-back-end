package edu.example.repository;

import edu.example.model.Comment;
import edu.example.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentsByPostOrderByCreatedAt(Post post);

    Page<Comment> findByPostIdOrderByCreatedAt(Long postId, Pageable pageable);
}
