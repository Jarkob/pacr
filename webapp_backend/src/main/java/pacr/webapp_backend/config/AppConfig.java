package pacr.webapp_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    private String allowedOrigins;

    public AppConfig(@NotNull @Value("${allowedOrigins}") String allowedOrigins) {
        Objects.requireNonNull(allowedOrigins);

        this.allowedOrigins = allowedOrigins;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedHeaders("*")
                .allowedMethods("*");
    }

}
