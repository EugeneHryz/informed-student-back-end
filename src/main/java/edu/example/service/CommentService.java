package edu.example.service;

import edu.example.dto.comment.CreateCommentRequestDto;
import edu.example.exception.EntityNotFoundException;
import edu.example.model.Comment;
import edu.example.model.User;
import edu.example.repository.CommentRepository;
import edu.example.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.history.Revisions;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /**
     * Get comments associated with a given post, sorted by creation time.
     * @param pageNumber page number, starting with 0
     * @param pageSize size of elements on the page
     * @param postId if of a post
     * @return Page object with information such as total number of posts, total number of pages, etc.
     */
    public Page<Comment> getCommentsByPost(int pageNumber, int pageSize, long postId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.desc("createdAt")
        ));
        return commentRepository.findByPostId(postId, pageable);
    }

    public Comment getComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment not found"));
    }

    public Comment createComment(CreateCommentRequestDto createRequestDto, User user) {
        var comment = new Comment();
        comment.setPost(postRepository.getReferenceById(createRequestDto.getPostId()));
        comment.setUser(user);
        comment.setText(createRequestDto.getText());
        comment.setAnonymous(createRequestDto.isAnonymous());
        return commentRepository.save(comment);
    }

    public Comment updateComment(Long id, String text) {
        var comment = commentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Comment not found"));
        comment.setText(text);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        commentRepository.deleteById(id);
    }

    /**
     * Deletes all comments that are at least {@code years}, {@code months}, {@code days} old
     * @param years number of years
     * @param months number of months
     * @param days number of days
     */
    public void deleteCommentsOlderThen(int years, int months, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -years);
        calendar.add(Calendar.MONTH, -months);
        calendar.add(Calendar.DATE, -days);

        commentRepository.deleteAllByCreatedAtBefore(new Timestamp(calendar.getTimeInMillis()));
    }

    public Revisions<Long, Comment> getCommentHistory(Long id) {
        return commentRepository.findRevisions(id);
    }

}