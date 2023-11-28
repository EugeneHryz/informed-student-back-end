package edu.example.web.security.handler;

import edu.example.model.Token;
import edu.example.service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static edu.example.web.security.SecurityConstants.JWT_COOKIE_NAME;

@RequiredArgsConstructor
@Component
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    private final TokenService tokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> tokenCookieOpt = Optional.empty();
        if (Objects.nonNull(cookies)) {
            tokenCookieOpt = Arrays.stream(cookies)
                    .filter(c -> JWT_COOKIE_NAME.equals(c.getName()))
                    .findFirst();
        }

        if (tokenCookieOpt.isPresent()) {

            String tokenValue = tokenCookieOpt.get().getValue();
            Optional<Token> tokenOptional = tokenService.findByTokenValue(tokenValue);
            tokenOptional.ifPresent(tokenService::makeInactive);

            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
}
