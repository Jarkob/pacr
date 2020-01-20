package pacr.webapp_backend.result_management.services;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.result_management.BenchmarkPropertyResult;
import pacr.webapp_backend.result_management.BenchmarkResult;
import pacr.webapp_backend.result_management.CommitResult;
import pacr.webapp_backend.shared.EventCategory;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.IEventHandler;
import pacr.webapp_backend.shared.ResultInterpretation;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Saves benchmarking results that were generated by a benchmarker of the pacr system. Can create a new event and
 * update observers of INewestResult.
 */
@Component
public class ResultBenchmarkSaver extends ResultSaver {

    private static final String TITLE_FORMAT = "New Benchmarking Result for Repository %d";
    private static final String GENERIC_DESCRIPTION_FORMAT =
            "A new benchmarking result was measured for the commit '%s' from repository '%d'. ";
    private static final String NO_COMPARISON_DESCRIPTION = "No data was found for comparison.";
    private static final String COMPARISON_DESCRIPTION =
            "On average, the new benchmarking result is %d percent %s then the previous one (commit '%s').";
    private static final String TITLE_FORMAT_GLOBAL_ERROR = "Error while benchmarking commit for repository '%d'";
    private static final String DESCRIPTION_FORMAT_GLOBAL_ERROR =
            "An error occurred while benchmarking commit '%s' for repository '%d': '%s'";
    private static final String POSITIVE = "better";
    private static final String NEGATIVE = "worse";

    private ResultGetter subjectForObservers;
    private IEventHandler eventHandler;
    private IGetCommitAccess commitAccess;

    /**
     * Creates a ResultBenchmarkSaver with access to results and a benchmark manager.
     * @param resultAccess access to results in storage.
     * @param benchmarkManager a benchmark manager to add newly detected benchmarks.
     * @param resultGetter the result getter that is the subject for observers that need to be updated.
     * @param eventHandler the event handler that events can be added to.
     * @param commitAccess access to ICommits.
     */
    ResultBenchmarkSaver(IResultAccess resultAccess, BenchmarkManager benchmarkManager, ResultGetter resultGetter,
                         IEventHandler eventHandler, IGetCommitAccess commitAccess) {
        super(resultAccess, benchmarkManager);
        this.subjectForObservers = resultGetter;
        this.eventHandler = eventHandler;
        this.commitAccess = commitAccess;
    }

    /**
     * Creates a new event with comparison to the comparison commit and updates observers of INewestResult.
     * @param result the result. Throws IllegalArgumentException if this is null.
     * @param comparisonCommitHash the hash of the commit for comparison. May be null (in this case no comparison is
     *                             done).
     */
    @Override
    void updateOtherComponents(@NotNull CommitResult result, @Nullable String comparisonCommitHash) {
        if (result == null) {
            throw new IllegalArgumentException("result cannot be null");
        }

        ICommit commit = commitAccess.getCommit(result.getCommitHash());
        CommitResult comparisonResult = null;

        if (comparisonCommitHash != null) {
            comparisonResult = resultAccess.getResultFromCommit(comparisonCommitHash);
        }

        eventHandler.addEvent(EventCategory.BENCHMARKING, generateTitle(commit, result),
                generateDescription(commit, result, comparisonResult));

        subjectForObservers.updateAll();
    }

    @Deprecated
    private String generateTitle(ICommit commit, CommitResult result) {
        if (result.hasGlobalError()) {
            // TODO this should be the repository's name
            return String.format(TITLE_FORMAT_GLOBAL_ERROR, commit.getRepositoryID());
        }

        return String.format(TITLE_FORMAT, commit.getRepositoryID());
    }

    @Deprecated
    private String generateDescription(ICommit commit, CommitResult result, CommitResult comparisonResult) {
        if (result.hasGlobalError()) {
            // TODO should be repository's name
            return String.format(DESCRIPTION_FORMAT_GLOBAL_ERROR, commit.getCommitHash(),
                    commit.getRepositoryID(), result.getGlobalError());
        }

        String description = String.format(GENERIC_DESCRIPTION_FORMAT, commit.getCommitHash(),
                commit.getRepositoryID());

        if (comparisonResult == null) {
            description += NO_COMPARISON_DESCRIPTION;
        } else {
            int averageImprovementPercentage = averageImprovementPercentage(result, comparisonResult);

            String positiveOrNegative = averageImprovementPercentage < 0 ? NEGATIVE : POSITIVE;

            description += String.format(COMPARISON_DESCRIPTION, Math.abs(averageImprovementPercentage),
                    positiveOrNegative, comparisonResult.getCommitHash());
        }

        return description;
    }

    private int averageImprovementPercentage(CommitResult result, CommitResult comparisonResult) {
        double totalImprovementPercentage = 0;
        int numberOfComparisons = 0;

        Iterable<BenchmarkResult> resultBenchmarks = result.getBenchmarksIterable();
        Map<String, BenchmarkResult> comparisonBenchmarks = comparisonResult.getBenchmarks();

        for (BenchmarkResult resultBenchmark : resultBenchmarks) {
            BenchmarkResult comparisonBenchmark = comparisonBenchmarks.get(resultBenchmark.getName());

            if (comparisonBenchmark != null) {
                Iterable<BenchmarkPropertyResult> resultProperties = resultBenchmark.getPropertiesIterable();
                Map<String, BenchmarkPropertyResult> comparisonProperties =
                        comparisonBenchmark.getBenchmarkProperties();

                for (BenchmarkPropertyResult resultProperty : resultProperties) {
                    BenchmarkPropertyResult comparisonProperty = comparisonProperties.get(resultProperty.getName());

                    if (comparisonProperty != null && !resultProperty.isError() && !comparisonProperty.isError()) {
                        totalImprovementPercentage += getImprovementPercentage(resultProperty, comparisonProperty);
                        ++numberOfComparisons;
                    }
                }
            }
        }

        if (numberOfComparisons <= 0) {
            return 0;
        }

        return (int) Math.round(totalImprovementPercentage / numberOfComparisons);
    }

    private double getImprovementPercentage(BenchmarkPropertyResult result, BenchmarkPropertyResult comparison) {
        double differencePercentage = (result.getMean() / comparison.getMean()) - 1;

        if (result.getResultInterpretation() == ResultInterpretation.LESS_IS_BETTER) {
            differencePercentage = -differencePercentage;
        }

        return differencePercentage;
    }
}
