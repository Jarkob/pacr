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

    private static final int HASH_LENGTH = 7;

    // TODO get these Strings from a file.
    private static final String TITLE_FORMAT = "'%s' Benchmarked for Repository '%s'";
    private static final String NO_COMPARISON_DESCRIPTION = "No data was found for comparison.";
    private static final String COMPARISON_DESCRIPTION =
            "On average, the result is %d%% %s then the previous one ('%s').";
    private static final String TITLE_FORMAT_GLOBAL_ERROR = "Error While Benchmarking '%s' for Repository '%s'";
    private static final String DESCRIPTION_FORMAT_GLOBAL_ERROR =
            "Error message: '%s'";
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
     * @param commitHash the hash of the commit that was benchmarked. Will be shortened to HASH_LENGTH characters.
     *                   Cannot be null.
     * @param repositoryName the name of the repository of the commit. Cannot be null.
     * @param globalError the error message of the result for the commit. May be null if there was no error. Otherwise
     *                    I assume an error (even if this field is empty or blank).
     * @param averageImprovementPercentage the average improvement between this commit and the comparison commit. Not
     *                                     used if comparisonCommitHash is null.
     * @param comparisonCommitHash the hash of the commit used for comparison. Will be shortened to HASH_LENGTH
     *                             characters. May be null (in this case it is assumed no comparison has taken place and
     *                             averageImprovementPercentage is ignored).
     */
    NewResultEvent(@NotNull EventCategory category, @NotNull String commitHash, @NotNull String repositoryName,
                          @Nullable String globalError, int averageImprovementPercentage,
                          @Nullable String comparisonCommitHash) {
        super(category);

        Objects.requireNonNull(category);
        Objects.requireNonNull(commitHash);
        Objects.requireNonNull(repositoryName);

        this.commitHash = shortenHash(commitHash);
        this.repositoryName = repositoryName;
        this.globalError = globalError;
        this.averageImprovementPercentage = averageImprovementPercentage;
        this.comparisonCommitHash = shortenHash(comparisonCommitHash);
    }

    @Override
    protected String buildTitle() {
        if (globalError != null) {
            // Following the definition in IBenchmarkingResult, I assume there was an error if globalError is not null
            // (even if global error is blank or empty)
            return String.format(TITLE_FORMAT_GLOBAL_ERROR, commitHash, repositoryName);
        }
        // I only assume there was no error if the field globalError is null.

        return String.format(TITLE_FORMAT, commitHash, repositoryName);
    }

    @Override
    protected String buildDescription() {
        if (globalError != null) {
            // Following the definition in IBenchmarkingResult, I assume there was an error if globalError is not null
            // (even if global error is blank or empty)
            return String.format(DESCRIPTION_FORMAT_GLOBAL_ERROR, globalError);
        }
        // I only assume there was no error if the field globalError is null.

        StringBuilder descriptionBuilder = new StringBuilder();

        if (comparisonCommitHash == null) {
            descriptionBuilder.append(NO_COMPARISON_DESCRIPTION);
        } else {
            String positiveOrNegative = averageImprovementPercentage < 0 ? NEGATIVE : POSITIVE;

            descriptionBuilder.append(String.format(COMPARISON_DESCRIPTION, Math.abs(averageImprovementPercentage),
                    positiveOrNegative, comparisonCommitHash));
        }

        return descriptionBuilder.toString();
    }

    private String shortenHash(String commitHash) {
        if (commitHash != null && commitHash.length() > HASH_LENGTH) {
            return commitHash.substring(0, HASH_LENGTH);
        }
        return commitHash;
    }
}
