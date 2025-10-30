package nl.hva.elections.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

/**
 * @class AppConfig
 * @description A helper configuration class. We created this to solve a startup issue (a circular dependency).
 * Its only job is to correctly create the AuthenticationManager bean.
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
}