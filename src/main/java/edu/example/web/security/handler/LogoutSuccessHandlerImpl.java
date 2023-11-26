package edu.example.web.security.handler;

import edu.example.model.Token;
import edu.example.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static edu.example.web.security.SecurityConstants.AUTH_HEADER;
import static edu.example.web.security.SecurityConstants.TOKEN_PREFIX;

@RequiredArgsConstructor
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    private final TokenService tokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String authHeaderValue = request.getHeader(AUTH_HEADER);
        if (Objects.nonNull(authHeaderValue) && authHeaderValue.startsWith(TOKEN_PREFIX)) {

            String tokenValue = authHeaderValue.substring(TOKEN_PREFIX.length());
            Optional<Token> tokenOptional = tokenService.findByTokenValue(tokenValue);
            tokenOptional.ifPresent(tokenService::makeInactive);

            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
