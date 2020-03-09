package pacr.webapp_backend.git_tracking.services;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;

/**
 * Represents the pull interval scheduler.
 * Executes the getPullInterval method in a variable interval,
 * stored in the database.
 */
@EnableScheduling
@Service
public class PullIntervalScheduler implements SchedulingConfigurer, NextExecutionGetter {

    private static final Logger LOGGER = LogManager.getLogger(PullIntervalScheduler.class);

    private final GitTracking gitTracking;
    private final TaskScheduler poolScheduler;
    private final IPullIntervalAccess pullIntervalAccess;

    private ScheduledFuture<?> currentTask;

    @Getter
    private LocalDateTime nextExecutionTime;

    /**
     * Creates an instance of PullIntervalScheduler.
     * @param gitTracking is used for accessing the pullFromAllRepositories() method.
     * @param poolScheduler is the TaskScheduler for executing the tasks.
     * @param pullIntervalAccess is needed for the next execution time.
     * @param pullIntervalDefault is the default value for the pull interval.
     */
    public PullIntervalScheduler(@NotNull final GitTracking gitTracking, @NotNull final TaskScheduler poolScheduler,
                                 @NotNull final IPullIntervalAccess pullIntervalAccess,
                                 @Value("${gitRepositoryPullIntervalDefault}") final int pullIntervalDefault) {
        Objects.requireNonNull(gitTracking);
        Objects.requireNonNull(poolScheduler);
        Objects.requireNonNull(pullIntervalAccess);

        this.gitTracking = gitTracking;
        this.poolScheduler = poolScheduler;
        this.pullIntervalAccess = pullIntervalAccess;

        try {
            pullIntervalAccess.getPullInterval();
        } catch (final NoSuchElementException e) {
            pullIntervalAccess.setPullInterval(pullIntervalDefault);
        }

    }

    @Override
    public void configureTasks(@NotNull final ScheduledTaskRegistrar taskRegistrar) {
        Objects.requireNonNull(taskRegistrar);

        taskRegistrar.setScheduler(poolScheduler);

        scheduleNextTask();
    }

    private void scheduleNextTask() {
        final int pullInterval = pullIntervalAccess.getPullInterval();
        final LocalDateTime nextExecution = LocalDateTime.now().plusSeconds(pullInterval);
        final Date nextExecutionDate = Date.from(nextExecution.atZone(ZoneId.systemDefault()).toInstant());

        currentTask = poolScheduler.scheduleWithFixedDelay(this::pullFromRepositories,
                nextExecutionDate, pullInterval * 1000);

        logNextExecution(nextExecution);
    }

    private void logNextExecution(final LocalDateTime nextExecution) {
        this.nextExecutionTime = nextExecution;

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String formatDateTime = nextExecution.format(formatter);
        LOGGER.info("Next execution time: {}", formatDateTime);
    }

    private void pullFromRepositories() {
        currentTask.cancel(false);

        LOGGER.info("Pulling from all repositories.");
        gitTracking.pullFromAllRepositories();

        scheduleNextTask();
    }
}
