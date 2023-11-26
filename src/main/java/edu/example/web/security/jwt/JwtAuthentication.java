package edu.example.web.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Objects;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private final String token;

    private String username;
    private UserDetails userDetails;

    public JwtAuthentication(String token) {
        super(List.of(new SimpleGrantedAuthority("USER")));
        this.token = token;
    }

    public JwtAuthentication(String token, String username, UserDetails userDetails) {
        super(List.of(new SimpleGrantedAuthority("USER")));
        this.token = token;
        this.username = username;
        this.userDetails = userDetails;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return Objects.nonNull(userDetails) ? userDetails : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}
