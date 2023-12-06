package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.User;
import edu.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final CommentService commentService;

    public Page<User> getUsers(Boolean isBanned, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.desc("id")
        ));

        if (nonNull(isBanned)) {
            return userRepository.findAllByBannedIs(isBanned, pageable);
        }
        return userRepository.findAll(pageable);
    }

    public List<User> getUsersByUsernameOrEmail(String searchTerm) {
        return userRepository.findByUsernameOrEmail(searchTerm);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User with specified username not found"));
    }

    /**
     * Set user's new ban status. If the user is getting banned, then all their tokens will be deactivated.
     * This way the user won't be able to perform any authorized requests after ban.
     * @param userId id of a user
     * @param isBanned new ban status
     * @return updated user instance
     */
    @Transactional
    public User updateUserBanStatus(Long userId, boolean isBanned) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (isBanned) {
            tokenService.deactivateUserTokens(user.getId());
        }
        user.setBanned(isBanned);
        user = userRepository.save(user);
        return user;
    }

    /**
     * Get user by his comment. This method is used only by admin to reveal identity of an
     * anonymous commentator.
     * @param commentId id of a comment
     * @return user instance
     */
    public User getUserByCommentId(Long commentId) {
        return commentService.getComment(commentId).getUser();
    }
}