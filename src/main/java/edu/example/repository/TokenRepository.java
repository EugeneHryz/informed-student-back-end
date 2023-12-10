package edu.example.repository;

import edu.example.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String tokenValue);

    List<Token> findByUserIdAndIsActive(long userId, boolean isActive);

    void deleteAllByUser_Id(long userId);
}
