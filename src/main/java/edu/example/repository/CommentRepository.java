package edu.example.repository;

import java.util.List;
import edu.example.model.Comment;
import edu.example.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findCommentsByPostOrderByCreatedAt(Post post);

    Page<Comment> findByPostId(Long postId, Pageable pageable);
}
