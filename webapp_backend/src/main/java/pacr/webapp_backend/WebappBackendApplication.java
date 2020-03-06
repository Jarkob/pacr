package pacr.webapp_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the spring application.
 */
@SpringBootApplication
public class WebappBackendApplication {

    /**
     * Runs the webapp backend
     * @param args arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(WebappBackendApplication.class, args);
    }
}
