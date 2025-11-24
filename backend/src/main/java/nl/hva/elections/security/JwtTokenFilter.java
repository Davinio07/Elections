package nl.hva.elections.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Dit is de hoofdfilterlogica die voor elke request wordt uitgevoerd.
     * Het bevat nu robuuste logica om de token uit de Authorization header of de WebSocket query parameter te halen.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Probeer de token uit de Authorization header te halen (standaard REST)
        String token = jwtTokenProvider.resolveToken(request);

        // 2. FIX: Probeer de token uit de query parameter te halen (gebruikt door STOMP/WebSocket)
        if (token == null && request.getQueryString() != null) {
            String queryString = request.getQueryString();
            String tokenParam = "token=";

            // Zoek naar "token=" in de query string
            int tokenStartIndex = queryString.indexOf(tokenParam);
            if (tokenStartIndex != -1) {
                // Start index na "token="
                String tokenSegment = queryString.substring(tokenStartIndex + tokenParam.length());

                // Stop bij de volgende '&' of het einde van de string
                int tokenEndIndex = tokenSegment.indexOf('&');
                if (tokenEndIndex != -1) {
                    token = tokenSegment.substring(0, tokenEndIndex);
                } else {
                    token = tokenSegment; // Token is het laatste item in de query
                }
            }
        }

        // 3. Controleer en authenticeer de gebruiker
        if (token != null && jwtTokenProvider.validateToken(token)) {

            String username = jwtTokenProvider.getUsername(token);

            // Laad de gebruiker uit de database (via UserService)
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Maak een nieuw authenticatie object en stel deze in als de huidige geauthenticeerde gebruiker
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 4. Altijd doorgaan naar de volgende filter
        filterChain.doFilter(request, response);
    }
}