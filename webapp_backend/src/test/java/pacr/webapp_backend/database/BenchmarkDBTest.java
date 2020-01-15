package pacr.webapp_backend.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BenchmarkDBTest {

    private static final String BENCHMARK_NAME = "benchmark";
    private static final String BENCHMARK_NAME_TWO = "benchmark2";
    private static final String GROUP_NAME = "group";
    private static final String GROUP_NAME_TWO = "group2";
    private static final int EXPECTED_NUM_OF_BENCHMARKS = 2;

    private BenchmarkDB benchmarkDB;

    @Autowired
    public BenchmarkDBTest(BenchmarkDB benchmarkDB) {
        this.benchmarkDB = benchmarkDB;
    }

    @BeforeEach
    public void setUp() {
        benchmarkDB.deleteAll();
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
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        benchmark.setGroup(group);

        benchmarkDB.saveBenchmark(benchmark);
        int benchmarkId = benchmark.getId();
        int groupId = group.getId();

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmarkId);

        assertEquals(groupId, savedBenchmark.getGroup().getId());
    }

    /**
     * Tests whether a group changes for all saved benchmarks if one of them is changed.
     */
    @Test
    public void saveBenchmark_updateGroup_getBenchmarkShouldReturnUpdate() {
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        benchmark.setGroup(group);
        group.addBenchmark(benchmark);

        Benchmark benchmarkTwo = new Benchmark(BENCHMARK_NAME_TWO);
        benchmarkTwo.setGroup(group);
        group.addBenchmark(benchmarkTwo);

        benchmarkDB.saveBenchmark(benchmark);
        int benchmarkId = benchmark.getId();
        benchmarkDB.saveBenchmark(benchmarkTwo);
        int benchmarkIdTwo = benchmarkTwo.getId();

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmarkId);
        savedBenchmark.getGroup().setName(GROUP_NAME_TWO);
        benchmarkDB.saveBenchmark(savedBenchmark);

        Benchmark savedBenchmarkTwo = benchmarkDB.getBenchmark(benchmarkIdTwo);

        assertEquals(GROUP_NAME_TWO, savedBenchmarkTwo.getGroup().getName());
    }
}
