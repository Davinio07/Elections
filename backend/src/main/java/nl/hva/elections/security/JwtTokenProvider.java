package nl.hva.elections.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @class JwtTokenProvider
 * @description A helper class that handles all things JWT.
 * Its main job is to create a token after a user logs in successfully.
 */
@Component
public class JwtTokenProvider {

    // For now, we generate a random secret key every time the app starts.
    // For production, we should use a fixed secret key from a config file (like .env or application.properties).
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // This defines how long the token is valid in milliseconds.
    private final long validityInMilliseconds = 3600000; // 1 hour

    /**
     * Creates a JWT token from a successful login.
     * @param authentication The official "proof of login" object from Spring Security.
     * @return A signed JWT token string.
     */
    public String createToken(Authentication authentication) {
        // The authentication object contains the username of the logged-in user.
        String username = authentication.getName();

        // Claims are pieces of information we put inside the token.
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        // We could add more stuff here later, like user roles.

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        // Build the token
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }
}