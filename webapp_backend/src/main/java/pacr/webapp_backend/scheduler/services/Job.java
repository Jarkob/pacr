package pacr.webapp_backend.scheduler.services;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IJob;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * A job that is identified by an id and belongs to a job group.
 * A job is queued when it is first created.
 */
@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class Job implements IJob {

    @Id
    @GeneratedValue
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private int id;

    @Setter
    @Getter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    private boolean prioritized;

    private String jobID;

    @EqualsAndHashCode.Exclude
    private LocalDateTime queued;

    @ManyToOne(fetch = FetchType.EAGER)
    private JobGroup group;

    /**
     * Creates a new job and sets its queued date.
     * @param jobID the id of the job.
     * @param group the group of the job.
     */
    Job(@NotNull final String jobID, @NotNull final JobGroup group) {
        if (!StringUtils.hasText(jobID)) {
            throw new IllegalArgumentException("The jobID cannot be null.");
        }
        Objects.requireNonNull(group);

        this.jobID = jobID;
        this.group = group;
        this.queued = LocalDateTime.now();
        this.prioritized = false;
    }

    /**
     * @return the current time sheet of the job's job group in seconds.
     */
    long getGroupTimeSheet() {
        return group.getTimeSheet();
    }

    @Override
    public String getJobGroupTitle() {
        return group.getTitle();
    }

}
