package pacr.webapp_backend.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.result_management.services.Benchmark;
import pacr.webapp_backend.result_management.services.BenchmarkGroup;
import pacr.webapp_backend.result_management.services.BenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BenchmarkDBTest extends SpringBootTestWithoutShell {

    private static final String BENCHMARK_NAME = "benchmark";
    private static final String BENCHMARK_NAME_TWO = "benchmark2";
    private static final String GROUP_NAME = "group";
    private static final String GROUP_NAME_TWO = "group2";
    private static final String PROPERTY_NAME = "property";
    private static final String UNIT = "unit";
    private static final int EXPECTED_NUM_OF_BENCHMARKS = 2;
    private static final int EXPECTED_NUM_OF_PROPERTIES = 1;
    private static final int EXPECTED_NUM_OF_BENCHMARKS_OF_GROUP = 1;

    private BenchmarkDB benchmarkDB;
    private BenchmarkGroupDB groupDB;

    @Autowired
    public BenchmarkDBTest(BenchmarkDB benchmarkDB, BenchmarkGroupDB groupDB) {
        this.benchmarkDB = benchmarkDB;
        this.groupDB = groupDB;
    }

    @AfterEach
    public void setUp() {
        benchmarkDB.deleteAll();
        groupDB.deleteAll();
    }

    /**
     * Tests whether a benchmark can be saved with saveBenchmark and retrieved with getBenchmark
     */
    @Test
    public void saveBenchmark_saveInDatabase_getBenchmarkShouldReturnBenchmark() {
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        benchmarkDB.saveBenchmark(benchmark);
        int id = benchmark.getId();

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(id);

        assertEquals(BENCHMARK_NAME, savedBenchmark.getOriginalName());
    }

    /**
     * Tests whether getAllBenchmarks returns the correct number of entered benchmarks.
     */
    @Test
    public void getAllBenchmarks_benchmarksInDatabase_ShouldReturnAll() {
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        Benchmark benchmarkTwo = new Benchmark(BENCHMARK_NAME_TWO);
        benchmarkDB.saveBenchmark(benchmark);
        benchmarkDB.saveBenchmark(benchmarkTwo);

        Collection<Benchmark> allBenchmarks = benchmarkDB.getAllBenchmarks();

        assertEquals(EXPECTED_NUM_OF_BENCHMARKS, allBenchmarks.size());
    }

    /**
     * Tests whether using save can update a benchmark that is already in the database.
     */
    @Test
    public void saveBenchmark_updateBenchmark_getBenchmarkShouldReturnUpdate() {
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        benchmarkDB.saveBenchmark(benchmark);
        int originalId = benchmark.getId();

        benchmark.setCustomName(BENCHMARK_NAME_TWO);
        benchmarkDB.saveBenchmark(benchmark);
        assertEquals(originalId, benchmark.getId());

        Benchmark updatedBenchmark = benchmarkDB.getBenchmark(originalId);

        assertEquals(BENCHMARK_NAME_TWO, updatedBenchmark.getCustomName());
    }

    /**
     * Tests whether benchmarks from the database have their original groups
     */
    @Test
    public void getBenchmark_retrieveGroup_ShouldReturnSameGroup() {
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        groupDB.saveBenchmarkGroup(group);

        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);

        benchmark.setGroup(group);

        benchmarkDB.saveBenchmark(benchmark);
        int benchmarkId = benchmark.getId();
        int groupId = group.getId();

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmarkId);

        assertEquals(groupId, savedBenchmark.getGroup().getId());
    }

    /**
     * Tests whether a group changes for saved benchmarks if it is changed.
     */
    @Test
    public void saveBenchmark_updateGroup_getBenchmarkShouldReturnUpdate() {
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        groupDB.saveBenchmarkGroup(group);

        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);

        benchmark.setGroup(group);

        benchmarkDB.saveBenchmark(benchmark);
        int benchmarkId = benchmark.getId();

        group.setName(GROUP_NAME_TWO);
        groupDB.saveBenchmarkGroup(group);

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmarkId);

        assertEquals(GROUP_NAME_TWO, savedBenchmark.getGroup().getName());
    }

    /**
     * Tests whether saveBenchmark also saves the associated properties if new ones were added.
     */
    @Test
    public void saveBenchmark_newProperty_getBenchmarkShouldReturnNewProperty() {
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        benchmarkDB.saveBenchmark(benchmark);

        BenchmarkProperty property = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER);
        benchmark.addProperty(property);

        benchmarkDB.saveBenchmark(benchmark);

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(EXPECTED_NUM_OF_PROPERTIES, savedBenchmark.getProperties().size());
        assertEquals(PROPERTY_NAME, savedBenchmark.getProperties().iterator().next().getName());
    }

    /**
     * Tests whether getBenchmarksOfGroup only returns the benchmarks of that group.
     */
    @Test
    void getBenchmarksOfGroup_twoGroupsTwoBenchmarks_shouldOnlyReturnBenchmarkOfGroup() {
        Benchmark benchmarkOne = new Benchmark(BENCHMARK_NAME);
        Benchmark benchmarkTwo = new Benchmark(BENCHMARK_NAME_TWO);

        BenchmarkGroup groupOne = new BenchmarkGroup(GROUP_NAME);
        benchmarkOne.setGroup(groupOne);
        BenchmarkGroup groupTwo = new BenchmarkGroup(GROUP_NAME_TWO);
        benchmarkTwo.setGroup(groupTwo);

        groupDB.saveBenchmarkGroup(groupOne);
        groupDB.saveBenchmarkGroup(groupTwo);
        benchmarkDB.saveBenchmark(benchmarkOne);
        benchmarkDB.saveBenchmark(benchmarkTwo);

        BenchmarkGroup savedGroupOne = groupDB.getBenchmarkGroup(groupOne.getId());
        Collection<Benchmark> benchmarksOfGroupOne = benchmarkDB.getBenchmarksOfGroup(savedGroupOne);

        assertEquals(EXPECTED_NUM_OF_BENCHMARKS_OF_GROUP, benchmarksOfGroupOne.size());
        assertEquals(BENCHMARK_NAME, benchmarksOfGroupOne.iterator().next().getOriginalName());
    }

    /**
     * Tests whether getBenchmarksOfGroup gets benchmarks without a group if null is given.
     */
    @Test
    void getBenchmarkOfGroup_groupIsNull_shouldReturnBenchmarkWithoutGroup() {
        Benchmark benchmarkOne = new Benchmark(BENCHMARK_NAME);
        Benchmark benchmarkTwo = new Benchmark(BENCHMARK_NAME_TWO);

        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        benchmarkOne.setGroup(group);

        groupDB.saveBenchmarkGroup(group);
        benchmarkDB.saveBenchmark(benchmarkOne);
        benchmarkDB.saveBenchmark(benchmarkTwo);

        Collection<Benchmark> benchmarksOfGroupOne = benchmarkDB.getBenchmarksOfGroup(null);

        assertEquals(EXPECTED_NUM_OF_BENCHMARKS_OF_GROUP, benchmarksOfGroupOne.size());
        assertEquals(BENCHMARK_NAME_TWO, benchmarksOfGroupOne.iterator().next().getOriginalName());
    }
}
