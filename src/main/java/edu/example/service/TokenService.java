package edu.example.service;

import edu.example.model.Token;
import edu.example.model.User;
import edu.example.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public Token saveNewToken(String tokenValue, User user) {
        Token token = new Token();
        token.setToken(tokenValue);
        token.setActive(true);
        token.setUser(user);
        return tokenRepository.save(token);
    }

    public Optional<Token> findByTokenValue(String token) {
        return tokenRepository.findByToken(token);
    }

    public void makeInactive(Token token) {
        token.setActive(false);
        tokenRepository.save(token);
    }

    public void deactivateUserTokens(Long userId) {
        List<Token> activeTokens = tokenRepository.findByUserIdAndIsActive(userId, true);
        activeTokens.forEach(token -> token.setActive(false));
        tokenRepository.saveAll(activeTokens);
    }
}
