package pacr.webapp_backend.result_management.services;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkProperty;
import pacr.webapp_backend.result_management.BenchmarkPropertyResult;
import pacr.webapp_backend.result_management.BenchmarkResult;
import pacr.webapp_backend.result_management.CommitResult;
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
import java.util.Set;

/**
 * Saves benchmarking results for commits and may update other components.
 */
@Component
abstract class ResultSaver {

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
     * @param result the benchmarking result that is saved. Cannot be null.
     * @param commit the commit of the benchmarking result. Cannot be null.
     * @param comparisonCommitHash the hash of the commit that is used for comparison when updating other components.
     *                             May be null. In that case no comparison will be executed.
     */
    void saveResult(@NotNull IBenchmarkingResult result, @NotNull ICommit commit,
                    @Nullable String comparisonCommitHash) {
        Objects.requireNonNull(result);
        Objects.requireNonNull(commit);

        Collection<Benchmark> savedBenchmarks = benchmarkManager.getAllBenchmarks();
        Collection<Benchmark> benchmarksFromResult = new HashSet<>();

        CommitResult comparisonResult = null;
        if (comparisonCommitHash != null) {
            comparisonResult = resultAccess.getResultFromCommit(comparisonCommitHash);
        }

        Set<BenchmarkResult> benchmarkResultsToSave = new HashSet<>();

        Map<String, ? extends IBenchmark> inputBenchmarkResultsMap = result.getBenchmarks();

        Map<String, BenchmarkResult> comparisonBenchmarkResultsMap = new HashMap<>();
        if (comparisonResult != null) {
            comparisonBenchmarkResultsMap = comparisonResult.getBenchmarks();
        }

        for (String inputBenchmarkName : inputBenchmarkResultsMap.keySet()) {
            IBenchmark inputBenchmarkResult = inputBenchmarkResultsMap.get(inputBenchmarkName);
            BenchmarkResult comparisonBenchmarkResult = comparisonBenchmarkResultsMap.get(inputBenchmarkName);

            Benchmark benchmark = getBenchmark(inputBenchmarkName, savedBenchmarks);
            benchmarksFromResult.add(benchmark);

            Set<BenchmarkPropertyResult> propertyResultsToSave = getPropertyResults(inputBenchmarkResult,
                    comparisonBenchmarkResult, benchmark);
            BenchmarkResult benchmarkResultToSave = new BenchmarkResult(propertyResultsToSave, benchmark);

            benchmarkResultsToSave.add(benchmarkResultToSave);
        }

        updateSavedBenchmarks(benchmarksFromResult);

        CommitResult resultToSave = new CommitResult(result, benchmarkResultsToSave, commit.getRepositoryID(),
                comparisonCommitHash);

        resultAccess.saveResult(resultToSave);

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

    private Set<BenchmarkPropertyResult> getPropertyResults(IBenchmark inputBenchmarkResult,
                                                            BenchmarkResult comparisonBenchmarkResult,
                                                            Benchmark benchmark) {
        Set<BenchmarkPropertyResult> propertyResultsToSave = new HashSet<>();

        Map<String, ? extends IBenchmarkProperty> inputPropertyResultsMap =
                inputBenchmarkResult.getBenchmarkProperties();

        Map<String, BenchmarkPropertyResult> comparisonPropertyResultsMap = new HashMap<>();
        if (comparisonBenchmarkResult != null) {
            comparisonPropertyResultsMap = comparisonBenchmarkResult.getBenchmarkProperties();
        }

        for (String inputPropertyName : inputPropertyResultsMap.keySet()) {
            IBenchmarkProperty inputPropertyResult = inputPropertyResultsMap.get(inputPropertyName);
            BenchmarkPropertyResult comparisonPropertyResult = comparisonPropertyResultsMap.get(inputPropertyName);

            BenchmarkPropertyResult propertyResultToSave = getPropertyResultUpdateBenchmark(inputPropertyName,
                    inputPropertyResult, comparisonPropertyResult, benchmark);

            propertyResultsToSave.add(propertyResultToSave);
        }

        return propertyResultsToSave;
    }

    private BenchmarkPropertyResult getPropertyResultUpdateBenchmark(String propertyName,
                                                                     IBenchmarkProperty propertyResult,
                                                                     BenchmarkPropertyResult comparisonPropertyResult,
                                                                     Benchmark benchmark) {
        // new property is created or it is found in the properties of the (saved or newly created) benchmark.
        BenchmarkProperty property = new BenchmarkProperty(propertyName, propertyResult.getUnit(),
                propertyResult.getResultInterpretation(), benchmark);

        for (BenchmarkProperty savedProperty : benchmark.getProperties()) {
            if (savedProperty.getName().equals(propertyName)) {
                property = savedProperty;
                break;
            }
        }
        benchmark.addProperty(property);

        BenchmarkPropertyResult propertyResultToSave =  new BenchmarkPropertyResult(propertyResult, property);

        // comparision to previous result
        if (comparisonPropertyResult != null && !propertyResultToSave.isError()
                && !comparisonPropertyResult.isError()) {
            propertyResultToSave.setRatio(propertyResultToSave.getMean() / comparisonPropertyResult.getMean());
            propertyResultToSave.setCompared(true);
        }

        return propertyResultToSave;
    }

    private Benchmark getBenchmark(String benchmarkName, Collection<Benchmark> savedBenchmarks) {
        // new Benchmark is created or it is found in saved benchmarks.
        Benchmark benchmark = new Benchmark(benchmarkName);
        for (Benchmark savedBenchmark : savedBenchmarks) {
            if (savedBenchmark.getOriginalName().equals(benchmarkName)) {
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
