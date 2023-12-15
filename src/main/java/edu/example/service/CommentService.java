package edu.example.service;

import edu.example.dto.comment.CreateCommentReplyRequestDto;
import edu.example.dto.comment.CreateCommentRequestDto;
import edu.example.exception.EntityNotFoundException;
import edu.example.model.Comment;
import edu.example.model.CommentReply;
import edu.example.model.User;
import edu.example.repository.CommentReplyRepository;
import edu.example.repository.CommentRepository;
import edu.example.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentReplyRepository commentReplyRepository;
    private final PostRepository postRepository;

    /**
     * Get comments associated with a given post, sorted by creation time.
     * @param pageNumber page number, starting with 0
     * @param pageSize size of elements on the page
     * @param postId id of a post
     * @return Page object with information such as total number of posts, total number of pages, etc.
     */
    public Page<Comment> getCommentsByPost(int pageNumber, int pageSize, long postId) {
        postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.desc("createdAt")
        ));
        return commentRepository.findByPostId(postId, pageable);
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    public Comment createComment(CreateCommentRequestDto createRequestDto, User author) {
        var comment = new Comment();
        comment.setPost(postRepository.getReferenceById(createRequestDto.getPostId()));
        comment.setUser(author);
        comment.setText(createRequestDto.getText());
        comment.setAnonymous(createRequestDto.isAnonymous());
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, String text) {
        var comment = commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Comment not found"));
        comment.setText(text);
        commentRepository.save(comment);
        return comment;
    }

    /**
     * Creates new comment and links it as a reply to a provided comment {@code dto.getCommentId()}.
     * @param dto contains comment id to which the reply is created, text of a reply and isAnonymous flag
     * @param author author of a reply
     * @return created reply
     */
    @Transactional
    public Comment addReplyToComment(CreateCommentReplyRequestDto dto, User author) {
        var comment = commentRepository.findById(dto.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (Objects.nonNull(comment.getPost().getFolder())) {
            throw new UnsupportedOperationException("Adding replies to comments under study materials posts is not supported");
        }

        var reply = new Comment();
        reply.setPost(comment.getPost());
        reply.setUser(author);
        reply.setText(dto.getText());
        reply.setAnonymous(dto.isAnonymous());
        commentRepository.save(reply);

        CommentReply commentReply = new CommentReply();
        commentReply.setComment(comment);
        commentReply.setReply(reply);
        commentReplyRepository.save(commentReply);
        return reply;
    }

    public Page<Comment> getCommentReplies(Long commentId, int pageNumber, int pageSize) {
        commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.desc("reply.createdAt")
        ));
        var commentRepliesPage = commentReplyRepository.findByCommentId(commentId, pageable);
        return commentRepliesPage.map(CommentReply::getReply);
    }

    @Transactional
    public void deleteComment(Long id) {
        commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        commentReplyRepository.deleteById(id);
        commentRepository.deleteById(id);
    }

    public Integer countNumberOfReplies(Long commentId) {
        return commentReplyRepository.countByCommentId(commentId);
    }

    /**
     * Deletes all comments that are at least {@code years}, {@code months}, {@code days} old
     * @param years number of years
     * @param months number of months
     * @param days number of days
     */
    @Transactional
    public void deleteCommentsOlderThen(int years, int months, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -years);
        calendar.add(Calendar.MONTH, -months);
        calendar.add(Calendar.DATE, -days);

        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

        commentReplyRepository.deleteAllByReply_CreatedAtBefore(timestamp);
        commentRepository.deleteAllByCreatedAtBefore(timestamp);
    }

    public Revisions<Long, Comment> getCommentHistory(Long id) {
        return commentRepository.findRevisions(id);
    }

}