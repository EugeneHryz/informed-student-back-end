package edu.example.web.security.jwt;

import edu.example.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.security.jwt.secret-key}")
    private String secretKey;

    @Value("${app.security.jwt.expiration-time}")
    private long jwtExpiration;

    public String generateFromUser(User user) {
        return buildToken(new HashMap<>(), user, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        Instant currentDate = Instant.now();
        Instant expirationDate = currentDate.plus(expiration, ChronoUnit.MILLIS);

        return Jwts.builder()
                .claims().add(extraClaims)
                .and()
                .subject(user.getUsername())
                .issuedAt(Date.from(currentDate))
                .expiration(Date.from(expirationDate))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        Claims allClaims = extractAllClaims(token);
        return claimResolver.apply(allClaims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
