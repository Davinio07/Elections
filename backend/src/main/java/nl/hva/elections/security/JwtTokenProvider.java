package nl.hva.elections.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @class JwtTokenProvider
 * @description A helper class that handles all things JWT.
 * Its main job is to create a token after a user logs in successfully,
 * and to validate tokens on incoming requests.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    // This is the secret key we use to sign our tokens.
    // It's generated randomly every time the app starts.
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // This is how long the token is valid in milliseconds.
    private final long validityInMilliseconds = 3600000;

    /**
     * Creates a JWT token from a successful login.
     * @param authentication The official "proof of login" object from Spring Security.
     * @return A signed JWT token string.
     */
    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Tries to get the token from the HTTP request.
     * It looks for the "Authorization" header and checks if it starts with "Bearer ".
     * @param req The incoming HTTP request.
     * @return The token string, or null if no token is found.
     */
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // "Bearer " is 7 characters long. We only want the token part.
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Checks if a given token is valid.
     * It checks the signature and the expiration date.
     * @param token The JWT token string to check.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            // The parser checks the signature and expiration date for us.
            // If it doesn't throw an error, the token is valid.
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // An error means the token is invalid
            logger.warn("Invalid JWT token attempted: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the username (the "subject") from a valid token.
     * @param token The JWT token string.
     * @return The username stored in the token.
     */
    public String getUsername(String token) {
        // We parse the token to get the data inside it
        Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
        // We stored the username in the "subject" field when we created the token.
        return claims.getSubject();
    }
}
