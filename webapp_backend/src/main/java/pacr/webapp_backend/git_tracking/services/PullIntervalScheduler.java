package pacr.webapp_backend.git_tracking.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Represents the pull interval scheduler.
 * Executes the getPullInterval method in a variable interval,
 * stored in the database.
 */
@EnableScheduling
@Service
public class PullIntervalScheduler implements SchedulingConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PullIntervalScheduler.class);

    private GitTracking gitTracking;
    private TaskScheduler poolScheduler;
    private PullIntervalSchedulingTrigger pullIntervalSchedulingTrigger;

    /**
     * Creates an instance of PullIntervalScheduler.
     * @param gitTracking is used for accessing the pullFromAllRepositories() method.
     * @param poolScheduler is the TaskScheduler for executing the tasks.
     * @param pullIntervalSchedulingTrigger is the trigger that calculates the next execution time.
     */
    public PullIntervalScheduler(@NotNull GitTracking gitTracking, @NotNull TaskScheduler poolScheduler,
                                 @NotNull PullIntervalSchedulingTrigger pullIntervalSchedulingTrigger) {
        Objects.requireNonNull(gitTracking);
        Objects.requireNonNull(poolScheduler);
        Objects.requireNonNull(pullIntervalSchedulingTrigger);

        this.gitTracking = gitTracking;
        this.poolScheduler = poolScheduler;
        this.pullIntervalSchedulingTrigger = pullIntervalSchedulingTrigger;
    }

    @Override
    public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
        Objects.requireNonNull(taskRegistrar);

        taskRegistrar.setScheduler(poolScheduler);
        taskRegistrar.addTriggerTask(this::pullFromRepositories, pullIntervalSchedulingTrigger);
    }

    private void pullFromRepositories() {
        LOGGER.info("Pulling from all repositories.");
        gitTracking.pullFromAllRepositories();
    }
}