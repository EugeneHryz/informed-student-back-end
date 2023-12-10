package edu.example.web.security.method;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.Comment;
import edu.example.repository.CommentRepository;
import edu.example.web.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component("commentSecurity")
public class CommentSecurity {

    private final CommentRepository commentRepository;

    public boolean isAllowedToModifyComment(Authentication auth, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException("Unable to find comment by id"));

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return Objects.equals(comment.getUser().getId(), userDetails.getUser().getId());
    }
}
