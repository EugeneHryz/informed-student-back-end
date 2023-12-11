package edu.example.web.security.method;

import edu.example.dto.post.CreatePostRequestDto;
import edu.example.exception.EntityNotFoundException;
import edu.example.model.Post;
import edu.example.model.Role;
import edu.example.repository.PostRepository;
import edu.example.web.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component("postSecurity")
public class PostSecurity {

    private final PostRepository postRepository;

    public boolean isAllowedToModifyPost(Authentication auth, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException("Unable to find post by id"));

        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return Objects.equals(post.getUser().getId(), userDetails.getUser().getId());
    }

    public boolean isAllowedToCreateNewsPost(Authentication auth, CreatePostRequestDto dto) {
        boolean hasModeratorAuthority = auth.getAuthorities().stream()
                .anyMatch(ga -> ga.getAuthority().equals(Role.MODERATOR.name()));

        return !Objects.isNull(dto.getFolderId()) || hasModeratorAuthority;
    }
}
