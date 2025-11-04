package nl.hva.elections.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 * This is where we define the basic information about our API, like the title,
 * version, and description. It's a clean way to keep configuration
 * separate from our business logic.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates a custom OpenAPI bean. This bean provides the information
     * that will be displayed at the top of the Swagger UI page.
     *
     * @return A configured OpenAPI object.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Elections API")
                        .version("v1.0")
                        .description("This is the REST API for the Elections project of HvA HBO-ICT.")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}