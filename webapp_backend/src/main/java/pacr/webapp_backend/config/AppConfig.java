package pacr.webapp_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Implements methods to add cors mapping.
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    private String allowedOrigins;

    /**
     * Initializes the app config.
     * @param allowedOrigins
     */
    public AppConfig(@NotNull @Value("${allowedOrigins}") final String allowedOrigins) {
        Objects.requireNonNull(allowedOrigins);

        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedHeaders("*")
                .allowedMethods("*");
    }

}
