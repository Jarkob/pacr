package pacr.webapp_backend.result_management.services;

import org.springframework.lang.Nullable;
import pacr.webapp_backend.shared.EventCategory;
import pacr.webapp_backend.shared.EventTemplate;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * EventTemplate for a new benchmarking result.
 */
public class NewResultEvent extends EventTemplate {

    // TODO get these Strings from a file.
    private static final String TITLE_FORMAT = "New Benchmarking Result for Repository '%s'";
    private static final String GENERIC_DESCRIPTION_FORMAT =
            "A new benchmarking result was measured for the commit '%s' from repository '%s'. ";
    private static final String NO_COMPARISON_DESCRIPTION = "No data was found for comparison.";
    private static final String COMPARISON_DESCRIPTION =
            "On average, the new benchmarking result is %d percent %s then the previous one (commit '%s').";
    private static final String TITLE_FORMAT_GLOBAL_ERROR = "Error While Benchmarking Commit for Repository '%s'";
    private static final String DESCRIPTION_FORMAT_GLOBAL_ERROR =
            "An error occurred while benchmarking commit '%s' for repository '%s': '%s'";
    private static final String POSITIVE = "better";
    private static final String NEGATIVE = "worse";

    private String commitHash;
    private String repositoryName;
    private String globalError;
    private int averageImprovementPercentage;
    private String comparisonCommitHash;

    /**
     * Creates a NewResultEvent for a new benchmarking result.
     *
     * @param category the category of the created events. Cannot be null.
     * @param commitHash the hash of the commit that was benchmarked. Cannot be null.
     * @param repositoryName the name of the repository of the commit. Cannot be null.
     * @param globalError the error message of the result for the commit. May be null if there was no error.
     * @param averageImprovementPercentage the average improvement between this commit and the comparison commit. Not
     *                                     used if comparisonCommitHash is null.
     * @param comparisonCommitHash the hash of the commit used for comparison. May be null (in this case it is assumed
     *                             no comparison has taken place and averageImprovementPercentage is ignored).
     */
    public NewResultEvent(@NotNull EventCategory category, @NotNull String commitHash, @NotNull String repositoryName,
                          @Nullable String globalError, int averageImprovementPercentage,
                          @Nullable String comparisonCommitHash) {
        super(category);

        Objects.requireNonNull(category);
        Objects.requireNonNull(commitHash);
        Objects.requireNonNull(repositoryName);

        this.commitHash = commitHash;
        this.repositoryName = repositoryName;
        this.globalError = globalError;
        this.averageImprovementPercentage = averageImprovementPercentage;
        this.comparisonCommitHash = comparisonCommitHash;
    }

    @Override
    protected String buildTitle() {
        if (globalError != null) {
            return String.format(TITLE_FORMAT_GLOBAL_ERROR, repositoryName);
        }

        return String.format(TITLE_FORMAT, repositoryName);
    }

    @Override
    protected String buildDescription() {
        if (globalError != null) {
            return String.format(DESCRIPTION_FORMAT_GLOBAL_ERROR, commitHash, repositoryName, globalError);
        }

        String description = String.format(GENERIC_DESCRIPTION_FORMAT, commitHash, repositoryName);

        if (comparisonCommitHash == null) {
            description += NO_COMPARISON_DESCRIPTION;
        } else {
            String positiveOrNegative = averageImprovementPercentage < 0 ? NEGATIVE : POSITIVE;

            description += String.format(COMPARISON_DESCRIPTION, Math.abs(averageImprovementPercentage),
                    positiveOrNegative, comparisonCommitHash);
        }

        return description;
    }
}
