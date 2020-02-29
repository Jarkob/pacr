package pacr.webapp_backend.result_management.services;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.ICommit;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * Saves benchmarking results for commits and may update other components.
 */
@Component
abstract class ResultSaver {

    private static final String NO_RESULT_ERROR = "PACR received no benchmarking result for this commit";
    private static final String NO_PROPERTY_RESULT_ERROR = "PACR received no measurements for this property";

    protected IResultAccess resultAccess;
    private BenchmarkManager benchmarkManager;

    /**
     * Creates a new ResultSaver with access to results and a benchmark manager.
     * @param resultAccess access to results in storage.
     * @param benchmarkManager a benchmark manager to add newly detected benchmarks.
     */
    ResultSaver(IResultAccess resultAccess, BenchmarkManager benchmarkManager) {
        this.resultAccess = resultAccess;
        this.benchmarkManager = benchmarkManager;
    }

    /**
     * This is a template method.
     * Saves a benchmarking result and saves associated benchmark metadata that is new to the system. Updates other
     * components depending on the implementation of updateOtherComponents.
     * Any result for the given commit that has already been saved will be replaced by this new result.
     * Enters Benchmark.class monitor and exits it. Then enters CommitResult.class monitor.
     * @param inputResult the benchmarking result that is saved. Cannot be null.
     * @param commit the commit of the benchmarking result. Cannot be null.
     * @param comparisonCommitHash the hash of the commit that is used for comparison. May be null. In that case no
     *                             comparison will be executed.
     */
    void saveResult(@NotNull IBenchmarkingResult inputResult, @NotNull ICommit commit,
                    @Nullable String comparisonCommitHash) {
        Objects.requireNonNull(inputResult);
        Objects.requireNonNull(commit);

        CommitResult comparisonResult = null;
        if (comparisonCommitHash != null) {
            comparisonResult = resultAccess.getResultFromCommit(comparisonCommitHash);
        }

        CommitResult resultToSave = new CommitResult(inputResult, commit.getRepositoryID(), commit.getCommitDate(),
                comparisonCommitHash);

        // indicates that the new result was compared if a result for the comparison commit hash was found
        if (comparisonResult != null) {
            resultToSave.setCompared(true);
        }

        synchronized (Benchmark.class) {
            Collection<Benchmark> savedBenchmarks = benchmarkManager.getAllBenchmarks();
            Collection<Benchmark> benchmarksFromResult = new HashSet<>();

            Map<String, ? extends IBenchmark> inputBenchmarkResultsMap = inputResult.getBenchmarks();

            Map<String, BenchmarkResult> comparisonBenchmarkResultsMap = new HashMap<>();
            if (comparisonResult != null) {
                comparisonBenchmarkResultsMap = comparisonResult.getBenchmarks();
            }

            for (String inputBenchmarkName : inputBenchmarkResultsMap.keySet()) {
                IBenchmark inputBenchmarkResult = inputBenchmarkResultsMap.get(inputBenchmarkName);

                if (inputBenchmarkResult.getBenchmarkProperties().isEmpty()) {
                    // skips this benchmark if it has no properties
                    continue;
                }

                Benchmark benchmark = getBenchmark(inputBenchmarkName, savedBenchmarks);
                benchmarksFromResult.add(benchmark);

                BenchmarkResult benchmarkResultToSave = new BenchmarkResult(benchmark);
                BenchmarkResult comparisonBenchmarkResult = comparisonBenchmarkResultsMap.get(inputBenchmarkName);

                addPropertyResults(inputBenchmarkResult, benchmarkResultToSave,
                        comparisonBenchmarkResult);

                resultToSave.addBenchmarkResult(benchmarkResultToSave);
            }

            updateSavedBenchmarks(benchmarksFromResult);
        }

        resultToSave.updateSignificance();

        // set error if it has not been set yet but there are no benchmark results
        if (resultToSave.hasNoBenchmarkResults() && !resultToSave.hasGlobalError()) {
            resultToSave.setError(true);
            resultToSave.setErrorMessage(NO_RESULT_ERROR);
        }

        synchronized (CommitResult.class) {
            resultAccess.saveResult(resultToSave);
        }

        updateOtherComponents(resultToSave, commit, comparisonCommitHash);
    }

    /**
     * This is the primitive method that is implemented by the subclasses.
     * Updates other components based on the result and a commit to compare it to.
     * @param result the result. Cannot be null.
     * @param commit the commit of the result. Cannot be null.
     * @param comparisonCommitHash the hash of the commit for comparison. May be null (in this case no comparison was
     *                             done).
     */
    abstract void updateOtherComponents(@NotNull CommitResult result, @NotNull ICommit commit,
                                        @Nullable String comparisonCommitHash);

    private void addPropertyResults(IBenchmark inputBenchmarkResult, BenchmarkResult benchmarkResult,
                                    BenchmarkResult comparisonBenchmarkResult) {
        Map<String, ? extends IBenchmarkProperty> inputPropertyResultsMap =
                inputBenchmarkResult.getBenchmarkProperties();

        Map<String, BenchmarkPropertyResult> comparisonPropertyResultsMap = new HashMap<>();
        if (comparisonBenchmarkResult != null) {
            comparisonPropertyResultsMap = comparisonBenchmarkResult.getBenchmarkProperties();
        }

        Benchmark benchmark = benchmarkResult.getBenchmark();

        for (String inputPropertyName : inputPropertyResultsMap.keySet()) {
            IBenchmarkProperty inputPropertyResult = inputPropertyResultsMap.get(inputPropertyName);
            BenchmarkPropertyResult comparisonPropertyResult = comparisonPropertyResultsMap.get(inputPropertyName);

            BenchmarkProperty property = getProperty(inputPropertyName, inputPropertyResult, benchmark);
            benchmark.addProperty(property);

            BenchmarkPropertyResult propertyResultToSave = new BenchmarkPropertyResult(inputPropertyResult, property);

            // set error if it has not been set yet but there are no results for this property
            if (propertyResultToSave.hasNoMeasurements() && !propertyResultToSave.isError()) {
                propertyResultToSave.setError(true);
                propertyResultToSave.setErrorMessage(NO_PROPERTY_RESULT_ERROR);
            }

            // comparison to previous result
            StatisticalCalculator.compare(propertyResultToSave, comparisonPropertyResult);

            benchmarkResult.addPropertyResult(propertyResultToSave);
        }
    }

    private BenchmarkProperty getProperty(String propertyName, IBenchmarkProperty inputPropertyResult,
                                                Benchmark benchmark) {
        // new property is created or it is found in the properties of the (saved or newly created) benchmark.
        BenchmarkProperty property = new BenchmarkProperty(propertyName, inputPropertyResult.getUnit(),
                inputPropertyResult.getResultInterpretation());

        for (BenchmarkProperty savedProperty : benchmark.getProperties()) {
            if (savedProperty.equals(property)) {
                // update the saved property only if this new property does not have an error
                // otherwise the fields such as ResultInterpretation might not be set correctly
                if (!inputPropertyResult.isError()) {
                    savedProperty.copyMetadataFrom(inputPropertyResult);
                }
                
                property = savedProperty;
                break;
            }
        }

        return property;
    }

    private Benchmark getBenchmark(String benchmarkName, Collection<Benchmark> savedBenchmarks) {
        // new Benchmark is created or it is found in saved benchmarks.
        Benchmark benchmark = new Benchmark(benchmarkName);
        for (Benchmark savedBenchmark : savedBenchmarks) {
            if (savedBenchmark.equals(benchmark)) {
                benchmark = savedBenchmark;
                break;
            }
        }

        return benchmark;
    }

    private void updateSavedBenchmarks(Collection<Benchmark> benchmarksToUpdate) {
        for (Benchmark benchmarkToUpdate : benchmarksToUpdate) {
            benchmarkManager.createOrUpdateBenchmark(benchmarkToUpdate);
        }
    }
}
