package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.User;
import edu.example.repository.UserInfoRepository;
import edu.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;

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

    public User updateUserIsBanned(Long id, boolean isBanned) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setBanned(isBanned);
        user = userRepository.save(user);
        return user;
    }

    public void deleteUser(Long id) {
        var user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        try {
            userInfoRepository.deleteById(user.getUsername());
        } catch (EntityNotFoundException ignored) {}
        userRepository.deleteById(id);
    }
}