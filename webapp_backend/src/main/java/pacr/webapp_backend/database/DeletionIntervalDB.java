package pacr.webapp_backend.database;

import pacr.webapp_backend.dashboard_management.services.IDeletionIntervalAccess;

/**
 * This class has direct access to the database, regarding the deletion interval of dashboards.
 * It implements the {@link IDeletionIntervalAccess} interface.
 */
public class DeletionIntervalDB implements IDeletionIntervalAccess {

    static final String DELETION_INTERVAL_IDENTIFIER = "deletion-interval";

    private ConfigRepo configRepo;

    /**
     * Creates an instance of DeletionIntervalDB.
     * @param configRepo is the JPA ConfigRepository used to store the configuration.
     */
    public DeletionIntervalDB(ConfigRepo configRepo) {
        this.configRepo = configRepo;
    }

    @Override
    public long getDeletionInterval() {
        return Long.parseLong(configRepo.findById(DELETION_INTERVAL_IDENTIFIER).orElseGet(
                ConfigItem::new).getConfigValue());
    }

    @Override
    public void setDeletionInterval(long interval) {
        ConfigItem configItem = new ConfigItem();
        configItem.setConfigKey(DELETION_INTERVAL_IDENTIFIER);
        configItem.setConfigValue(String.valueOf(interval));
        configRepo.save(configItem);
    }
}
