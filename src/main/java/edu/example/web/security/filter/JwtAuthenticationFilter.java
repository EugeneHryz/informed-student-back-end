package edu.example.web.security.filter;

import edu.example.web.security.jwt.JwtAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import static edu.example.web.security.SecurityConstants.JWT_COOKIE_NAME;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        Optional<Cookie> tokenCookieOpt = Optional.empty();
        if (Objects.nonNull(cookies)) {
            tokenCookieOpt = Arrays.stream(cookies)
                    .filter(c -> JWT_COOKIE_NAME.equals(c.getName()))
                    .findFirst();
        }

        if (tokenCookieOpt.isPresent()) {

            String tokenValue = tokenCookieOpt.get().getValue();
            try {
                Authentication authResult = authenticationManager.authenticate(new JwtAuthentication(tokenValue));

                SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
                context.setAuthentication(authResult);
                securityContextHolderStrategy.setContext(context);
            } catch (AuthenticationException e) {
                securityContextHolderStrategy.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
