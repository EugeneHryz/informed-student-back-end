package edu.example.service;

import com.querydsl.core.types.Predicate;
import edu.example.exception.EntityNotFoundException;
import edu.example.model.Role;
import edu.example.model.User;
import edu.example.repository.TokenRepository;
import edu.example.repository.UserInfoRepository;
import edu.example.repository.UserRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static java.util.Objects.nonNull;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;
    private final CommentService commentService;
    private final UserInfoRepository userInfoRepository;

    public Page<User> getUsers(Predicate predicate, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(
                Sort.Order.asc("id")
        ));

        return userRepository.findAll(predicate, pageable);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User with specified username not found"));
    }

    /**
     * Set user's new ban status or role. If the user is getting banned, then all their tokens will be deactivated.
     * This way the user won't be able to perform any authorized requests after ban.
     * @param userId id of a user
     * @param isBanned new ban status
     * @param role new role
     * @return updated user instance
     */
    @Transactional
    public User updateUser(Long userId, @Nullable Boolean isBanned, @Nullable Role role) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Updating admin is not allowed");
        }

        if (nonNull(isBanned)) {
            if (isBanned) {
                tokenService.deactivateUserTokens(user.getId());
            }
            user.setBanned(isBanned);
        }
        if (nonNull(role)) {
            user.setRole(role);
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        if (user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Deleting admin account is not allowed");
        }
        try {
            userInfoRepository.deleteById(user.getUsername());
        } catch (EntityNotFoundException ignored) {}
        tokenRepository.deleteAllByUser_Id(id);
        userRepository.deleteById(id);
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