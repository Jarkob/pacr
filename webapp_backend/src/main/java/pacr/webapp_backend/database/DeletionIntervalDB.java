package pacr.webapp_backend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pacr.webapp_backend.dashboard_management.services.IDeletionIntervalAccess;

import java.util.NoSuchElementException;

/**
 * This class has direct access to the database, regarding the deletion interval of dashboards.
 * It implements the {@link IDeletionIntervalAccess} interface.
 */
@Service
public class DeletionIntervalDB implements IDeletionIntervalAccess {

    static final String DELETION_INTERVAL_IDENTIFIER = "deletion-interval";

    private ConfigRepo configRepo;

    /**
     * Creates an instance of DeletionIntervalDB.
     * @param configRepo is the JPA ConfigRepository used to store the configuration.
     */
    @Autowired
    public DeletionIntervalDB(ConfigRepo configRepo) {
        this.configRepo = configRepo;
    }

    @Override
    public long getDeletionInterval() throws NoSuchElementException {
        return Long.parseLong(configRepo.findById(DELETION_INTERVAL_IDENTIFIER).orElseThrow().getConfigValue());
    }

    @Override
    public void setDeletionInterval(long interval) {
        ConfigItem configItem = new ConfigItem();
        configItem.setConfigKey(DELETION_INTERVAL_IDENTIFIER);
        configItem.setConfigValue(String.valueOf(interval));
        configRepo.save(configItem);
    }

    @Override
    public void delete() {
        configRepo.delete(configRepo.getOne(DELETION_INTERVAL_IDENTIFIER));
    }
}
