package pacr.webapp_backend.git_tracking.services;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Represents a trigger that calculates the next execution time
 * by adding the pull interval to the last execution time.
 *
 * @author Pavel Zwerschke
 */
@Component
public class PullIntervalSchedulingTrigger implements Trigger, NextExecutionGetter {

    private static final Logger LOGGER = LogManager.getLogger(PullIntervalSchedulingTrigger.class);

    private IPullIntervalAccess pullIntervalAccess;

    @Getter
    private LocalDateTime nextExecutionTime;

    /**
     * Creates an instance of PullIntervalSchedulingTrigger.
     * @param pullIntervalAccess is the acces to the DB where the pull interval is stored.
     * @param pullIntervalDefault is the default value for the pull interval.
     *                            Gets set if there is no value for pull interval stored in the database.
     */
    public PullIntervalSchedulingTrigger(@NotNull IPullIntervalAccess pullIntervalAccess,
                                         @Value("${gitRepositoryPullIntervalDefault}") int pullIntervalDefault) {
        Objects.requireNonNull(pullIntervalAccess);

        this.pullIntervalAccess = pullIntervalAccess;
        try {
            pullIntervalAccess.getPullInterval();
        } catch (NoSuchElementException e) {
            pullIntervalAccess.setPullInterval(pullIntervalDefault);
        }
    }

    @Override
    public Date nextExecutionTime(@NotNull TriggerContext t) {
        Objects.requireNonNull(t);

        LocalDateTime now;
        if (t.lastActualExecutionTime() == null || t.lastActualExecutionTime().toInstant() == null) {
            now = LocalDateTime.now();
        } else {
            now = LocalDateTime.ofInstant(t.lastActualExecutionTime().toInstant(), ZoneId.systemDefault());
        }

        LocalDateTime nextExecution = now.plusSeconds(pullIntervalAccess.getPullInterval());

        logNextExecution(nextExecution);

        return Date.from(nextExecution.atZone(ZoneId.systemDefault()).toInstant());
    }

    private void logNextExecution(LocalDateTime nextExecution) {
        assert nextExecution != null;

        this.nextExecutionTime = nextExecution;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = nextExecution.format(formatter);
        LOGGER.info("Next execution time: {}", formatDateTime);
    }
}
