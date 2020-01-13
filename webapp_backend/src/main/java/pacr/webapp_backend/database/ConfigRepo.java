package pacr.webapp_backend.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * This is the JPA configuration repository.
 *
 * @author Pavel Zwerschke
 */
@Component
public interface ConfigRepo extends JpaRepository<ConfigItem, String> {
}
