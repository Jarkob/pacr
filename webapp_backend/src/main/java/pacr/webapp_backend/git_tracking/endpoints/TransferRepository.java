package pacr.webapp_backend.git_tracking.endpoints;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Transfer object for encoding repositories in JSON.
 *
 * @author Pavel Zwerschke
 */
@Getter @Setter
public class TransferRepository {

    private static final String WEBHOOK_PREFIX = "/webhooks/";

    private int id;
    private boolean trackAllBranches;
    private List<String> trackedBranches;
    private String pullURL;
    private String name;
    private boolean isHookSet;
    private String color;
    private LocalDate observeFromDate;
    private String commitLinkPrefix;
    private final String webHookURL;

    /**
     * Creates a new instance of TransferRepository.
     * @param id is the ID of the repository.
     * @param trackAllBranches is the option whether all branches are being tracked.
     * @param trackedBranches are all tracked branches.
     * @param pullURL is the pull URL of the repository.
     * @param name is the name of the repository.
     * @param isHookSet is the option whether a hook is set.
     * @param color is the color of the repository encoded in hex.
     * @param observeFromDate is the date from which on the repository is observed.
     * @param commitLinkPrefix is the commit link prefix.
     */
    public TransferRepository(int id, boolean trackAllBranches, List<String> trackedBranches, String pullURL,
                              String name, boolean isHookSet, String color, LocalDate observeFromDate,
                              String commitLinkPrefix) {
        this.id = id;
        this.trackAllBranches = trackAllBranches;
        this.trackedBranches = trackedBranches;
        this.pullURL = pullURL;
        this.name = name;
        this.isHookSet = isHookSet;
        this.color = color;
        this.observeFromDate = observeFromDate;
        this.commitLinkPrefix = commitLinkPrefix;
        this.webHookURL = WEBHOOK_PREFIX + id;
    }
}
