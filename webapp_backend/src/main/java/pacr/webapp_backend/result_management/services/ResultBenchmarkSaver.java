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
import java.util.Objects;

/**
 * Saves benchmarking results that were generated by a benchmarker of the pacr system. Can create a new event and
 * update observers of INewestResult.
 */
@Component
public class ResultBenchmarkSaver extends ResultSaver {

    private ResultGetter subjectForObservers;
    private IEventHandler eventHandler;

    /**
     * Creates a ResultBenchmarkSaver with access to results and a benchmark manager.
     * @param resultAccess access to results in storage.
     * @param benchmarkManager a benchmark manager to add newly detected benchmarks.
     * @param resultGetter the result getter that is the subject for observers that need to be updated.
     * @param eventHandler the event handler that events can be added to.
     */
    ResultBenchmarkSaver(IResultAccess resultAccess, BenchmarkManager benchmarkManager, ResultGetter resultGetter,
                         IEventHandler eventHandler) {
        super(resultAccess, benchmarkManager);
        this.subjectForObservers = resultGetter;
        this.eventHandler = eventHandler;
    }

    /**
     * Creates a new event with comparison to the comparison commit and updates observers of INewestResult.
     * @param result the result. Throws IllegalArgumentException if this is null.
     * @param comparisonCommitHash the hash of the commit for comparison. May be null (in this case no comparison is
     *                             done).
     */
    @Override
    void updateOtherComponents(@NotNull CommitResult result, @NotNull ICommit commit,
                               @Nullable String comparisonCommitHash) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(commit);

        int averageImprovementPercentage = 0;

        if (comparisonCommitHash != null) {
            CommitResult comparisonResult = resultAccess.getResultFromCommit(comparisonCommitHash);

            averageImprovementPercentage = averageImprovementPercentage(result, comparisonResult);
        }

        NewResultEvent benchmarkingEvent = new NewResultEvent(EventCategory.BENCHMARKING, commit.getCommitHash(),
                String.valueOf(commit.getRepositoryID()), result.getGlobalError(), averageImprovementPercentage,
                comparisonCommitHash);

        eventHandler.addEvent(benchmarkingEvent);

        subjectForObservers.updateAll();
    }

    private int averageImprovementPercentage(CommitResult result, CommitResult comparisonResult) {
        if (comparisonResult == null) {
            return 0;
        }

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

        return (int) Math.round((totalImprovementPercentage / numberOfComparisons) * 100d);
    }

    private double getImprovementPercentage(BenchmarkPropertyResult result, BenchmarkPropertyResult comparison) {
        double differencePercentage = (result.getMean() / comparison.getMean()) - 1;

        if (result.getResultInterpretation() == ResultInterpretation.LESS_IS_BETTER) {
            differencePercentage = -differencePercentage;
        }

        return differencePercentage;
    }
}