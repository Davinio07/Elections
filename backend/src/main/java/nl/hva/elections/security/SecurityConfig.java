package nl.hva.elections.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @class SecurityConfig
 * @description The main configuration for all security-related stuff.
 * We use this to disable the default Spring login page and set up our own rules for JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    /**
     * Constructor to inject our custom filter.
     * @param jwtTokenFilter Our custom filter that checks tokens.
     */
    @Autowired
    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }


    /**
     * This is the main security filter
     * It checks every request and decides if it's allowed or not.
     * @param http The HttpSecurity object from Spring to configure the rules.
     * @return The configured security filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF. Not needed for our stateless API.
                .csrf(csrf -> csrf.disable())
                // We're using tokens, so we dont need the server to remember users in a session.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                .authorizeHttpRequests(auth -> auth
                        // 1. Specifiek de routes aanwijzen die beveiligd moeten zijn.
                        // Alleen ingelogde gebruikers mogen de userlijst zien.
                        .requestMatchers(HttpMethod.GET, "/api/users").authenticated()
                        // hier onder kunnen meer regels bij komen voor andere beveiligde routes,
                        // zoals POST /api/forum/posts)

                        // 2. Al het ANDERE (inclusief alle GET /api/elections/...) is PUBLIEK.
                        .anyRequest().permitAll()
                );

        // Required for the H2 console to work correctly with Spring Security.
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        // Add our custom JWT filter before the default Spring Security filter that handles username/password authentication. 
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * This sets up the CORS rules.
     * @return A WebMvcConfigurer with our CORS rules.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);

                registry.addMapping("/ws/**") // WebSocket endpoint
                        .allowedOrigins("http://localhost:5173", "http://localhost:5176")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}


