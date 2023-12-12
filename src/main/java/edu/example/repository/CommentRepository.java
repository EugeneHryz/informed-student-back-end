package edu.example.repository;

import edu.example.model.Comment;
import edu.example.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, RevisionRepository<Comment, Long, Long> {

    List<Comment> findCommentsByPostOrderByCreatedAt(Post post);

    @Query(value = "select c from Comment c " +
            "left join CommentReply cr on cr.reply = c " +
            "where cr is null and c.post.id = :postId")
    @EntityGraph(attributePaths = {"user"})
    Page<Comment> findByPostId(Long postId, Pageable pageable);

    @Transactional
    void deleteAllByCreatedAtBefore(Timestamp timestamp);
}
