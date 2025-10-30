package nl.hva.elections.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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

    /**
     * This is the main security filter.
     * It checks every request and decides if it's allowed or not.
     * @param http The HttpSecurity object from Spring to configure the rules.
     * @return The configured security filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF. Not needed for our stateless API.
                .csrf(csrf -> csrf.disable())
                // We're using tokens, so we don't need the server to remember users in a session.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Here we define the access rules.
                .authorizeHttpRequests(auth -> auth
                        // We allow everyone to access our API endpoints for now.
                        // This is important so the login and register pages work.
                        .requestMatchers("/api/**").permitAll()
                        // Also allow everyone to see the H2 database console.
                        .requestMatchers("/h2-console/**").permitAll()
                        // Any other request needs to be authenticated (we'll add this later).
                        .anyRequest().authenticated()
                );

        // This is a small fix to make the H2 console display correctly.
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

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
            }
        };
    }

    /**
     * Creates the PasswordEncoder we use for hashing passwords.
     * By making it a @Bean, Spring knows about it and can give it to other classes,
     * like our UserService.
     * @return A BCrypt password encoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}