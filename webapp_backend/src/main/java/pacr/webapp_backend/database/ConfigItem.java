package pacr.webapp_backend.database;

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
public class ConfigItem {

    @Id
    private String configKey;

    private String configValue;

    /**
     * Creates an instance of ConfigItem
     */
    public ConfigItem() {
    }

    /**
     * Creates an instance of ConfigItem.
     * @param configKey is the configuration key.
     * @param configValue is the configuration value.
     */
    public ConfigItem(String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    /**
     * Returns the configuration key.
     * @return configKey.
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * Sets the configuration key.
     * @param configKey is the configuration key.
     */
    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    /**
     * Returns the configuration value.
     * @return configuration value.
     */
    public String getConfigValue() {
        return configValue;
    }

    /**
     * Sets the configuration value.
     * @param configValue is the configuration value.
     */
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
}

