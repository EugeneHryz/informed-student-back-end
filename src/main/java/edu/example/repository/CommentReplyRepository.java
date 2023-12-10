package edu.example.repository;

import edu.example.model.CommentReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {

    @EntityGraph(attributePaths = {"reply", "reply.user"})
    Page<CommentReply> findByCommentId(Long commentId, Pageable pageable);

    Integer countByCommentId(Long commentId);
}
