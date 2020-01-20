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

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
     * @param result the benchmarking result that is saved. Throws IllegalArgumentException if this is null.
     * @param comparisonCommitHash the hash of the commit that is used for comparison when updating other components.
     *                             May be null. In that case no comparison will be executed.
     */
    void saveResult(IBenchmarkingResult result, String comparisonCommitHash) {
        if (result == null) {
            throw new IllegalArgumentException("result cannot be null");
        }

        Collection<Benchmark> savedBenchmarks = benchmarkManager.getAllBenchmarks();

        Collection<Benchmark> benchmarksFromResult = new HashSet<>();

        Set<BenchmarkResult> benchmarkResultsToSave = new HashSet<>();

        Map<String, ? extends IBenchmark> inputBenchmarkResultsMap = result.getBenchmarks();

        for (String inputBenchmarkName : inputBenchmarkResultsMap.keySet()) {

            IBenchmark inputBenchmarkResult = inputBenchmarkResultsMap.get(inputBenchmarkName);

            Benchmark benchmark = getBenchmark(inputBenchmarkName, savedBenchmarks);

            Set<BenchmarkPropertyResult> propertyResultsToSave = new HashSet<>();

            Map<String, ? extends IBenchmarkProperty> inputPropertyResultsMap =
                    inputBenchmarkResult.getBenchmarkProperties();

            for (String inputPropertyName : inputPropertyResultsMap.keySet()) {
                IBenchmarkProperty inputPropertyResult = inputPropertyResultsMap.get(inputPropertyName);

                BenchmarkPropertyResult propertyResultToSave = getPropertyResultAndUpdateBenchmark(inputPropertyName,
                        inputPropertyResult, benchmark);

                propertyResultsToSave.add(propertyResultToSave);
            }

            benchmarksFromResult.add(benchmark);

            BenchmarkResult benchmarkResultToSave = new BenchmarkResult(propertyResultsToSave, benchmark);

            benchmarkResultsToSave.add(benchmarkResultToSave);
        }

        updateSavedBenchmarks(benchmarksFromResult);

        CommitResult resultToSave = new CommitResult(result, benchmarkResultsToSave);

        resultAccess.saveResult(resultToSave);

        updateOtherComponents(resultToSave, comparisonCommitHash);
    }

    /**
     * This is the primitive method that is implemented by the subclasses.
     * Updates other components based on the result and a commit to compare it to.
     * @param result the result. Throws IllegalArgumentException if this is null.
     * @param comparisonCommitHash the hash of the commit for comparison. May be null. (in this case no comparison is
     *                             done).
     */
    abstract void updateOtherComponents(@NotNull CommitResult result, @Nullable String comparisonCommitHash);

    private BenchmarkPropertyResult getPropertyResultAndUpdateBenchmark(String propertyName,
                                                                        IBenchmarkProperty propertyResult,
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

        return new BenchmarkPropertyResult(propertyResult, property);
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
