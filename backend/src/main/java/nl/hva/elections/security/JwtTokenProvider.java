package nl.hva.elections.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import nl.hva.elections.models.User;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for handling JWT token generation and validation.
 * This is a core part of our custom security implementation.
 */
@Component
public class JwtTokenProvider {

    // For now we generate a random secret key. Once we switch to a production environment,
    // we should use a fixed secret key stored in a secure place like .env
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // This number defines how long the token is valid in milliseconds.
    private final long validityInMilliseconds = 3600000; // 1 hour

    /**
     * Generates a JWT token for a given user.
     * @param user The user for whom the token should be generated.
     * @return A signed JWT token as a String.
     */
    public String createToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        // Here we can add more claims as needed, like roles like admin/user.

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }
}