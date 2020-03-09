package pacr.webapp_backend.result_management.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.EventCategory;
import pacr.webapp_backend.shared.ICommit;
import pacr.webapp_backend.shared.IEventHandler;
import pacr.webapp_backend.shared.INewestResult;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Saves benchmarking results that were generated by a benchmarker of the pacr system. Can create a new event and
 * update observers of INewestResult.
 */
@Component
public class ResultBenchmarkSaver extends ResultSaver {

    private static final double ONE_IN_PERCENT = 100.0d;

    private static final Logger LOGGER = LogManager.getLogger(ResultBenchmarkSaver.class);

    private final ResultGetter subjectForObservers;
    private final IEventHandler eventHandler;

    /**
     * Creates a ResultBenchmarkSaver with access to results and a benchmark manager.
     * @param resultAccess access to results in storage.
     * @param benchmarkManager a benchmark manager to add newly detected benchmarks.
     * @param resultGetter the result getter that is the subject for observers that need to be updated.
     * @param eventHandler the event handler that events can be added to.
     */
    ResultBenchmarkSaver(final IResultAccess resultAccess, final BenchmarkManager benchmarkManager,
                         final ResultGetter resultGetter, final IEventHandler eventHandler) {
        super(resultAccess, benchmarkManager);
        this.subjectForObservers = resultGetter;
        this.eventHandler = eventHandler;
    }

    /**
     * Creates a new event with comparison to the comparison commit and updates observers of {@link INewestResult} if
     * the new commit is on the master branch.
     * @param result the result. Cannot be null
     * @param commit the commit of the result. Cannot be null.
     * @param comparisonCommitHash the hash of the commit for comparison. May be null (in this case no comparison was
     *                             done).
     */
    @Override
    void updateOtherComponents(@NotNull final CommitResult result, @NotNull final ICommit commit,
                               @Nullable final String comparisonCommitHash) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(commit);

        double totalImprovementPercentage = 0;
        int numberOfComparisons = 0;

        for (final BenchmarkResult benchmarkResult : result.getBenchmarkResults()) {
            for (final BenchmarkPropertyResult propertyResult : benchmarkResult.getPropertyResults()) {
                if (propertyResult.isCompared()) {
                    totalImprovementPercentage += (propertyResult.getRatio() - 1.0d) * ONE_IN_PERCENT;
                    ++numberOfComparisons;
                }
            }
        }

        int averageImprovementPercentage = 0;
        if (numberOfComparisons > 0) {
            averageImprovementPercentage = (int) Math.round(totalImprovementPercentage / numberOfComparisons);
        }

        String isComparedHash = null;
        if (result.isCompared()) {
            isComparedHash = comparisonCommitHash;
        }

        final NewResultEvent benchmarkingEvent = new NewResultEvent(EventCategory.BENCHMARKING, commit.getCommitHash(),
                commit.getRepositoryName(), result.getGlobalError(), averageImprovementPercentage,
                isComparedHash);

        LOGGER.info("creating event for new result for commit {}", commit.getCommitHash());

        eventHandler.addEvent(benchmarkingEvent);

        // only update observers if the commit is on the master branch
        if (commit.isOnMaster()) {
            LOGGER.info("updating observers for new result for commit {}", commit.getCommitHash());
            subjectForObservers.updateAll();
        }
    }
}
