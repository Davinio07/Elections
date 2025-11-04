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

/**
 * @class JwtTokenFilter
 * @description This is our custom filter.
 * It extends OncePerRequestFilter, which means it runs once for every single
 * request that comes into our application.
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * Constructor so Spring can give us the tools we need.
     * @param jwtTokenProvider The helper class to read and check tokens.
     * @param userDetailsService Our UserService, which Spring knows how to find.
     */
    @Autowired
    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * This is the main method of the filter. All API requests go through here first.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Try to get the token from the request's "Authorization" header.
        String token = jwtTokenProvider.resolveToken(request);

        // 2. Check if we found a token AND if it's valid.
        if (token != null && jwtTokenProvider.validateToken(token)) {
            
            // 3. If the token is valid, get the username from it.
            String username = jwtTokenProvider.getUsername(token);

            // 4. Load the user details from our database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Create an auth object for Spring Security
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            
            // 6. Set this user as the currently authenticated user for this request.
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 7. Always pass the request to the next filter in the chain.
        // If we forget this, the request stops here and our app will hang!
        filterChain.doFilter(request, response);
    }
}
