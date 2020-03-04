package pacr.webapp_backend.database;

import org.springframework.stereotype.Component;
import pacr.webapp_backend.git_tracking.services.IPullIntervalAccess;

/**
 * This is an implementation of the IPullIntervalAccess interface.
 *
 * @author Pavel Zwerschke
 */
@Component
public class PullIntervalDB implements IPullIntervalAccess {
    private static final String PULL_INTERVAL_IDENTIFIER = "pull-interval";

    private ConfigRepo configRepo;

    /**
     * Creates an instance of PullIntervalDB.
     * @param configRepo is the JPA ConfigRepository needed to store the configuration.
     */
    public PullIntervalDB(final ConfigRepo configRepo) {
        this.configRepo = configRepo;
    }

    @Override
    public int getPullInterval() {
        return Integer.parseInt(configRepo.findById(PULL_INTERVAL_IDENTIFIER).orElseThrow().getConfigValue());
    }

    @Override
    public void setPullInterval(final int interval) {
        final ConfigItem configItem = new ConfigItem();
        configItem.setConfigKey(PULL_INTERVAL_IDENTIFIER);
        configItem.setConfigValue(String.valueOf(interval));
        configRepo.save(configItem);
    }
}
