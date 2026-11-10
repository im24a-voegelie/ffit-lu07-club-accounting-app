package ch.bzz.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String projectName) {
        Date currentTime = new Date();
        Date expirationTime = new Date(currentTime.getTime() + 3_600_000);

        return Jwts.builder()
                .setSubject(projectName)
                .setIssuer("AccountingApp")
                .setIssuedAt(currentTime)
                .setExpiration(expirationTime)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Reads the project name (subject) from an "Authorization: Bearer <token>" header.
     * Returns empty if the header is missing or the token is invalid or expired.
     */
    public Optional<String> extractProjectName(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }
        try {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            String subject = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            return Optional.ofNullable(subject);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
