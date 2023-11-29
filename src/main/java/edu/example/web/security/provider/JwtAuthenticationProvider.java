package edu.example.web.security.provider;

import edu.example.model.Token;
import edu.example.service.TokenService;
import edu.example.web.security.exception.InvalidTokenException;
import edu.example.web.security.jwt.JwtAuthentication;
import edu.example.web.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String tokenValue = (String) authentication.getCredentials();
        String subject;
        try {
            subject = jwtService.extractSubject(tokenValue);
        } catch (RuntimeException e) {
            throw new InvalidTokenException("Token is invalid", e);
        }
        Optional<Token> tokenOptional = tokenService.findByTokenValue(tokenValue);
        if (tokenOptional.isEmpty()) {
            return null;
        }
        Token token = tokenOptional.get();
        if (!token.isActive()) {
            throw new InvalidTokenException("Token is not active");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        JwtAuthentication auth = new JwtAuthentication(tokenValue, subject, userDetails);
        auth.setAuthenticated(true);
        return auth;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }
}
