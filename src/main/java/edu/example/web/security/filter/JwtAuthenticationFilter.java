package edu.example.web.security.filter;

import edu.example.web.security.jwt.JwtAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

import static edu.example.web.security.SecurityConstants.AUTH_HEADER;
import static edu.example.web.security.SecurityConstants.TOKEN_PREFIX;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private SecurityContextRepository securityContextRepository = new RequestAttributeSecurityContextRepository();

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        String authHeaderValue = request.getHeader(AUTH_HEADER);
        if (Objects.nonNull(authHeaderValue) && authHeaderValue.startsWith(TOKEN_PREFIX)) {

            String tokenValue = authHeaderValue.substring(TOKEN_PREFIX.length());

            try {
                Authentication autResult = authenticationManager.authenticate(new JwtAuthentication(tokenValue));

                SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
                context.setAuthentication(autResult);
                securityContextHolderStrategy.setContext(context);
                securityContextRepository.saveContext(context, request, response);
            } catch (AuthenticationException e) {
                securityContextHolderStrategy.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
