package pacr.webapp_backend.database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BenchmarkGroupDBTest {

    private static final String GROUP_NAME = "group";
    private static final String BENCHMARK_NAME = "benchmark";

    private BenchmarkGroupDB groupDB;
    private BenchmarkDB benchmarkDB;

    @Autowired
    public BenchmarkGroupDBTest(BenchmarkGroupDB groupDB, BenchmarkDB benchmarkDB) {
        this.benchmarkDB = benchmarkDB;
        this.groupDB = groupDB;
    }

    @BeforeEach
    public void setUp() {
        benchmarkDB.deleteAll();
        groupDB.deleteAll();
    }

    /**
     * Tests whether a group can be saved with saveBenchmarkGroup and retrieved with getBenchmarkGroup
     */
    @Test
    public void saveGroup_saveInDatabase_getGroupShouldReturnGroup() {
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        groupDB.saveBenchmarkGroup(group);
        int id = group.getId();

        BenchmarkGroup savedGroup = groupDB.getBenchmarkGroup(id);

        assertEquals(GROUP_NAME, savedGroup.getName());
    }

    /**
     * Tests whether a group that was added to a benchmark in benchmarkDB can be retrieved from groupDB.
     */
    @Test
    public void getGroup_saveFromBenchmarkDB_shouldReturnSameGroup() {
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        BenchmarkGroup group = new BenchmarkGroup(GROUP_NAME);
        benchmark.setGroup(group);
        group.addBenchmark(benchmark);

        benchmarkDB.saveBenchmark(benchmark);
        int groupId = group.getId();

        BenchmarkGroup savedGroup = groupDB.getBenchmarkGroup(groupId);

        assertEquals(GROUP_NAME, savedGroup.getName());
    }
}
