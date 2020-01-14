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
        BenchmarkGroup group = new BenchmarkGroup("group");
        groupDB.saveBenchmarkGroup(group);
        int id = group.getId();
        BenchmarkGroup savedGroup = groupDB.getBenchmarkGroup(id);
        assertEquals("group", savedGroup.getName());
    }

    /**
     * Tests whether a group that was added to a benchmark in benchmarkDB can be retrieved from groupDB.
     */
    @Test
    public void getGroup_saveFromBenchmarkDB_shouldReturnSameGroup() {
        Benchmark benchmark = new Benchmark("benchmark");
        BenchmarkGroup group = new BenchmarkGroup("group1325");
        benchmark.setGroup(group);
        group.addBenchmark(benchmark);

        benchmarkDB.saveBenchmark(benchmark);
        int groupId = group.getId();

        BenchmarkGroup savedGroup = groupDB.getBenchmarkGroup(groupId);
        assertEquals("group1325", savedGroup.getName());
    }
}
