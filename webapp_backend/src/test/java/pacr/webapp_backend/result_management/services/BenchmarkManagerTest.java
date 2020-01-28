package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.database.BenchmarkDB;
import pacr.webapp_backend.database.BenchmarkGroupDB;
import pacr.webapp_backend.result_management.BenchmarkProperty;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class BenchmarkManagerTest extends SpringBootTestWithoutShell {
    private BenchmarkDB benchmarkDB;
    private BenchmarkGroupDB groupDB;
    private BenchmarkManager benchmarkManager;

    private static final String BENCHMARK_NAME = "benchmark";
    private static final String BENCHMARK_NAME_TWO = "benchmark2";
    private static final String BENCHMARK_DESCRIPTION = "desc";
    private static final String GROUP_NAME = "group";
    private static final String GROUP_NAME_TWO = "groupTwo";
    private static final String PROPERTY_NAME = "property";
    private static final String PROPERTY_NAME_TWO = "propertyTwo";
    private static final String UNIT = "unit";
    private static final int EXPECTED_NUM_OF_PROPERTIES = 2;
    private static final int EXPECTED_NUM_OF_GROUPS = 2;
    private static final int EXPECTED_NUM_OF_GROUPS_AFTER_DEL = 0;
    private static final int NO_ID = 0;
    private static final int NO_GROUP_ID = -1;
    private static final int EXPECTED_NUM_OF_BENCHMARKS = 1;

    private Benchmark benchmark;
    private BenchmarkGroup group;

    @Autowired
    public BenchmarkManagerTest(BenchmarkDB benchmarkDB, BenchmarkGroupDB groupDB) {
        this.benchmarkDB = benchmarkDB;
        this.groupDB = groupDB;
        this.benchmarkManager = new BenchmarkManager(benchmarkDB, groupDB);
    }

    @BeforeEach
    public void setUp() {
        group = new BenchmarkGroup(GROUP_NAME);

        benchmark = new Benchmark(BENCHMARK_NAME);

        BenchmarkProperty property = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER,
                benchmark);
        benchmark.addProperty(property);

        benchmark.setGroup(group);

        groupDB.saveBenchmarkGroup(group);
    }

    @AfterEach
    public void cleanUp() {
        benchmarkDB.deleteAll();
        groupDB.deleteAll();
    }

    /**
     * Tests whether a benchmark saved with createOrUpdateBenchmark can be retrieved.
     */
    @Test
    public void createOrUpdateBenchmark_benchmarkNotYetSaved_shouldSaveBenchmark() {
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME_TWO);

        benchmarkManager.createOrUpdateBenchmark(benchmark);

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(BENCHMARK_NAME_TWO, savedBenchmark.getOriginalName());
        assertEquals(benchmark.getId(), savedBenchmark.getId());
    }

    /**
     * Tests whether createOrUpdateBenchmark also updates the list of associated properties.
     */
    @Test
    public void createOrUpdateBenchmark_addedProperty_shouldAlsoReturnAddedProperty() {
        benchmarkDB.saveBenchmark(benchmark);

        BenchmarkProperty newProperty = new BenchmarkProperty(PROPERTY_NAME_TWO, UNIT,
                ResultInterpretation.LESS_IS_BETTER, benchmark);

        benchmark.addProperty(newProperty);

        benchmarkManager.createOrUpdateBenchmark(benchmark);

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(EXPECTED_NUM_OF_PROPERTIES, savedBenchmark.getProperties().size());
    }

    /**
     * Tests whether createOrUpdateBenchmark can create a benchmark and then add a property to it. Also tests if the id
     * of the property gets set.
     */
    @Test
    public void createOrUpdateBenchmark_createAndAddProperty_shouldAlsoReturnAddedProperty() {
        benchmarkManager.createOrUpdateBenchmark(benchmark);

        BenchmarkProperty newProperty = new BenchmarkProperty(PROPERTY_NAME_TWO, UNIT,
                ResultInterpretation.LESS_IS_BETTER, benchmark);

        benchmark.addProperty(newProperty);

        benchmarkManager.createOrUpdateBenchmark(benchmark);

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(EXPECTED_NUM_OF_PROPERTIES, savedBenchmark.getProperties().size());
        assertNotEquals(NO_ID, newProperty.getId());
    }

    /**
     * Tests whether updateBenchmark changes the custom name of a benchmark in the database (and nothing else).
     */
    @Test
    public void updateBenchmark_newName_shouldOnlyChangeName() {
        benchmarkDB.saveBenchmark(benchmark);

        benchmarkManager.updateBenchmark(benchmark.getId(), BENCHMARK_NAME_TWO, BENCHMARK_DESCRIPTION, group.getId());

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(BENCHMARK_NAME_TWO, savedBenchmark.getCustomName());
        assertEquals(BENCHMARK_NAME, savedBenchmark.getOriginalName());
        assertEquals(BENCHMARK_DESCRIPTION, savedBenchmark.getDescription());
        assertEquals(group.getId(), savedBenchmark.getGroup().getId());
    }

    /**
     * Tests whether updateBenchmark changes the group of a benchmark in the database (and nothing else).
     */
    @Test
    public void updateBenchmark_newGroup_shouldOnlyChangeGroup() {
        benchmarkDB.saveBenchmark(benchmark);

        BenchmarkGroup groupTwo = new BenchmarkGroup(GROUP_NAME_TWO);
        groupDB.saveBenchmarkGroup(groupTwo);

        benchmarkManager.updateBenchmark(benchmark.getId(), BENCHMARK_NAME, BENCHMARK_DESCRIPTION, groupTwo.getId());

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(groupTwo.getId(), savedBenchmark.getGroup().getId());
        assertEquals(BENCHMARK_NAME, savedBenchmark.getCustomName());
        assertEquals(BENCHMARK_NAME, savedBenchmark.getOriginalName());
        assertEquals(BENCHMARK_DESCRIPTION, savedBenchmark.getDescription());
    }

    /**
     * Tests whether a benchmark can be removed from a group with updateBenchmark by passing the groupId
     * GROUP_ID_NO_GROUP.
     */
    @Test
    public void updateBenchmark_groupIdNoGroup_shouldMakeGroupNull() {
        benchmarkDB.saveBenchmark(benchmark);

        benchmarkManager.updateBenchmark(benchmark.getId(), BENCHMARK_NAME, BENCHMARK_DESCRIPTION,
                BenchmarkManager.GROUP_ID_NO_GROUP);

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertNull(savedBenchmark.getGroup());
    }

    /**
     * Tests whether group that was added with addGroup can be retrieved.
     */
    @Test
    public void addGroup_shouldSaveGroup() {
        benchmarkManager.addGroup(GROUP_NAME_TWO);

        assertEquals(EXPECTED_NUM_OF_GROUPS, benchmarkManager.getAllGroups().size());
    }

    /**
     * Tests whether updateGroup updates contents of group.
     */
    @Test
    public void updateGroup_newName_shouldChangeName() {
        benchmarkManager.updateGroup(group.getId(), GROUP_NAME_TWO);

        BenchmarkGroup savedGroup = groupDB.getBenchmarkGroup(group.getId());

        assertEquals(GROUP_NAME_TWO, savedGroup.getName());
    }

    /**
     * Tests whether deleteGroup removes the group from the database.
     */
    @Test
    public void deleteGroup_shouldRemoveGroupFromDatabase() {
        benchmarkManager.deleteGroup(group.getId());

        assertEquals(EXPECTED_NUM_OF_GROUPS_AFTER_DEL, groupDB.count());
    }

    /**
     * Tests whether deleteGroup also removes the group from an associated benchmark.
     */
    @Test
    public void deleteGroup_benchmarkHasGroup_shouldRemoveGroupFromBenchmark() {
        benchmarkDB.saveBenchmark(benchmark);

        benchmarkManager.deleteGroup(group.getId());

        Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertNull(savedBenchmark.getGroup());
    }

    /**
     * Tests whether getBenchmarksByGroup gets benchmarks with no group if given -1.
     */
    @Test
    public void getBenchmarksByGroup_parameterMinusOne_shouldReturnBenchmarksWithNoGroup() {
        benchmark.setGroup(null);
        benchmarkDB.saveBenchmark(benchmark);

        Collection<Benchmark> savedBenchmarksWithNoGroup = benchmarkManager.getBenchmarksByGroup(NO_GROUP_ID);

        assertEquals(EXPECTED_NUM_OF_BENCHMARKS, savedBenchmarksWithNoGroup.size());
        assertEquals(BENCHMARK_NAME, savedBenchmarksWithNoGroup.iterator().next().getOriginalName());
    }
}
