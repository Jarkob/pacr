package pacr.webapp_backend.database;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Represents a Configuration Item being stored in a database.
 * This item is used so that one can store different configuration
 * parameters in one table.
 * It contains a configuration key and a configuration value.
 *
 * @author Pavel Zwerschke
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
public class ConfigItem {

    @Id
    private String configKey;

    private String configValue;

}

