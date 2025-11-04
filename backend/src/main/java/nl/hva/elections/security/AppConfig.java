package nl.hva.elections.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @class AppConfig
 * @description A helper configuration class. We use this to solve startup issues
 * by creating beans that are needed by many other classes.
 */
@Configuration
public class AppConfig {

    /**
     * Creates the AuthenticationManager bean.
     * This is the main Spring component responsible for authenticating a user.
     * @param authConfig The standard authentication configuration from Spring.
     * @return The configured AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Creates the PasswordEncoder we use for hashing passwords.
     * We put it here to break the circular dependency with SecurityConfig.
     * @return A BCrypt password encoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
