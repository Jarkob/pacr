package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.database.BenchmarkDB;
import pacr.webapp_backend.database.BenchmarkGroupDB;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.Collection;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private static final int EXPECTED_NUM_OF_GROUPS = 3;
    private static final int EXPECTED_SINGLE = 1;
    private static final int NO_ID = 0;

    private Benchmark benchmark;
    private BenchmarkGroup group;

    @Autowired
    public BenchmarkManagerTest(final BenchmarkDB benchmarkDB, final BenchmarkGroupDB groupDB) {
        this.benchmarkDB = benchmarkDB;
        this.groupDB = groupDB;
        this.benchmarkManager = new BenchmarkManager(benchmarkDB, groupDB);
    }

    @BeforeEach
    public void setUp() {
        group = new BenchmarkGroup(GROUP_NAME);

        benchmark = new Benchmark(BENCHMARK_NAME);

        final BenchmarkProperty property = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER);
        benchmark.addProperty(property);

        benchmark.setGroup(group);

        groupDB.saveBenchmarkGroup(group);
    }

    @AfterEach
    public void cleanUp() {
        benchmarkDB.deleteAll();
        groupDB.deleteAll();

        this.benchmarkManager = new BenchmarkManager(benchmarkDB, groupDB);
    }

    /**
     * Tests whether a benchmark saved with createOrUpdateBenchmark can be retrieved.
     */
    @Test
    public void createOrUpdateBenchmark_benchmarkNotYetSaved_shouldSaveBenchmark() {
        final Benchmark benchmark = new Benchmark(BENCHMARK_NAME_TWO);

        benchmarkManager.createOrUpdateBenchmark(benchmark);

        final Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(BENCHMARK_NAME_TWO, savedBenchmark.getOriginalName());
        assertEquals(benchmark.getId(), savedBenchmark.getId());
    }

    /**
     * Tests whether createOrUpdateBenchmark also updates the list of associated properties.
     */
    @Test
    public void createOrUpdateBenchmark_addedProperty_shouldAlsoReturnAddedProperty() {
        benchmarkDB.saveBenchmark(benchmark);

        final BenchmarkProperty newProperty = new BenchmarkProperty(PROPERTY_NAME_TWO, UNIT,
                ResultInterpretation.LESS_IS_BETTER);

        benchmark.addProperty(newProperty);

        benchmarkManager.createOrUpdateBenchmark(benchmark);

        final Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(EXPECTED_NUM_OF_PROPERTIES, savedBenchmark.getProperties().size());
    }

    /**
     * Tests whether createOrUpdateBenchmark can create a benchmark and then add a property to it. Also tests if the id
     * of the property gets set.
     */
    @Test
    public void createOrUpdateBenchmark_createAndAddProperty_shouldAlsoReturnAddedProperty() {
        benchmarkManager.createOrUpdateBenchmark(benchmark);

        final BenchmarkProperty newProperty = new BenchmarkProperty(PROPERTY_NAME_TWO, UNIT,
                ResultInterpretation.LESS_IS_BETTER);

        benchmark.addProperty(newProperty);

        benchmarkManager.createOrUpdateBenchmark(benchmark);

        final Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

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

        final Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

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

        final BenchmarkGroup groupTwo = new BenchmarkGroup(GROUP_NAME_TWO);
        groupDB.saveBenchmarkGroup(groupTwo);

        benchmarkManager.updateBenchmark(benchmark.getId(), BENCHMARK_NAME, BENCHMARK_DESCRIPTION, groupTwo.getId());

        final Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(groupTwo.getId(), savedBenchmark.getGroup().getId());
        assertEquals(BENCHMARK_NAME, savedBenchmark.getCustomName());
        assertEquals(BENCHMARK_NAME, savedBenchmark.getOriginalName());
        assertEquals(BENCHMARK_DESCRIPTION, savedBenchmark.getDescription());
    }

    /**
     * Tests whether a benchmark can be removed from a group with updateBenchmark by passing the standard group id.
     */
    @Test
    public void updateBenchmark_groupIdStandard_shouldMakeGroupStandard() {
        benchmarkDB.saveBenchmark(benchmark);

        benchmarkManager.updateBenchmark(benchmark.getId(), BENCHMARK_NAME, BENCHMARK_DESCRIPTION,
                BenchmarkManager.getStandardGroupId());

        final Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(BenchmarkManager.getStandardGroupId(), savedBenchmark.getGroup().getId());
    }

    @Test
    void updateBenchmark_noBenchmarkWithGivenIdSaved_shouldNotUpdate() {
        IBenchmarkAccess benchmarkAccessSpy = Mockito.mock(IBenchmarkAccess.class);
        when(benchmarkAccessSpy.getBenchmark(benchmark.getId())).thenReturn(null);

        BenchmarkManager managerWithSpy = new BenchmarkManager(benchmarkAccessSpy, groupDB);

        managerWithSpy.updateBenchmark(benchmark.getId(), benchmark.getCustomName(), benchmark.getDescription(),
                group.getId());

        verify(benchmarkAccessSpy, never()).saveBenchmark(any());
    }

    @Test
    void updateBenchmark_noGroupWithGivenIdSaved_shouldNotUpdateBenchmark() {
        benchmarkDB.saveBenchmark(benchmark);

        benchmarkManager.updateBenchmark(benchmark.getId(), BENCHMARK_NAME_TWO, benchmark.getDescription(),
                group.getId() + 1);

        Benchmark newBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(group.getId(), newBenchmark.getGroup().getId());
        assertEquals(BENCHMARK_NAME, newBenchmark.getCustomName());
    }

    @Test
    void updateBenchmark_emptyName_shouldThrowException() {
        benchmarkDB.saveBenchmark(benchmark);

        assertThrows(IllegalArgumentException.class, () -> benchmarkManager.updateBenchmark(benchmark.getId(),
                "", benchmark.getDescription(), group.getId()));
    }

    /**
     * Tests whether group that was added with addGroup can be retrieved.
     */
    @Test
    public void addGroup_shouldSaveGroup() {
        benchmarkManager.addGroup(GROUP_NAME_TWO);

        assertEquals(EXPECTED_NUM_OF_GROUPS, benchmarkManager.getAllGroups().size());
    }

    @Test
    void addGroup_emptyName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> benchmarkManager.addGroup(""));
    }

    /**
     * Tests whether updateGroup updates contents of group.
     */
    @Test
    public void updateGroup_newName_shouldChangeName() {
        benchmarkManager.updateGroup(group.getId(), GROUP_NAME_TWO);

        final BenchmarkGroup savedGroup = groupDB.getBenchmarkGroup(group.getId());

        assertEquals(GROUP_NAME_TWO, savedGroup.getName());
    }

    @Test
    void updateGroup_emptyName_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> benchmarkManager.updateGroup(group.getId(), ""));
    }

    @Test
    void updateGroup_noGroupWithGivenIdSaved_shouldNotUpdate() {
        IBenchmarkGroupAccess groupAccessSpy = Mockito.mock(IBenchmarkGroupAccess.class);
        BenchmarkManager managerWithSpy = new BenchmarkManager(benchmarkDB, groupAccessSpy);

        when(groupAccessSpy.getBenchmarkGroup(group.getId())).thenReturn(null);

        managerWithSpy.updateGroup(group.getId(), GROUP_NAME_TWO);

        verify(groupAccessSpy, atMostOnce()).saveBenchmarkGroup(any());
    }

    /**
     * Tests whether deleteGroup removes the group from the database.
     */
    @Test
    public void deleteGroup_shouldRemoveGroupFromDatabase() throws IllegalAccessException {
        benchmarkManager.deleteGroup(group.getId());

        assertEquals(EXPECTED_SINGLE, groupDB.count());
    }

    /**
     * Tests whether deleteGroup also removes the group from an associated benchmark.
     */
    @Test
    public void deleteGroup_benchmarkHasGroup_shouldRemoveGroupFromBenchmark() throws IllegalAccessException {
        benchmarkDB.saveBenchmark(benchmark);

        benchmarkManager.deleteGroup(group.getId());

        final Benchmark savedBenchmark = benchmarkDB.getBenchmark(benchmark.getId());

        assertEquals(BenchmarkManager.getStandardGroupId(), savedBenchmark.getGroup().getId());
    }

    @Test
    void deleteGroup_attemptToDeleteStandardGroup_shouldThrowException() {
        final BenchmarkGroup standardGroup = groupDB.getStandardGroup();

        assertThrows(IllegalAccessException.class, () -> benchmarkManager.deleteGroup(standardGroup.getId()));
    }

    @Test
    void deleteGroup_noGroupWithGivenIdSaved_shouldThrowException() {
        final BenchmarkGroup standardGroup = groupDB.getStandardGroup();
        assertThrows(NoSuchElementException.class,
                () -> benchmarkManager.deleteGroup(standardGroup.getId() + group.getId()));
    }

    @Test
    void getBenchmarksByGroup_twoBenchmarksDifferentGroups_shouldOnlyReturnBenchmarkOfGroup() {
        final BenchmarkGroup groupTwo = new BenchmarkGroup(GROUP_NAME_TWO);
        final Benchmark benchmarkTwo = new Benchmark(BENCHMARK_NAME_TWO);
        benchmarkTwo.setGroup(groupTwo);

        groupDB.saveBenchmarkGroup(groupTwo);
        benchmarkDB.saveBenchmark(benchmarkTwo);

        final Collection<Benchmark> benchmarks = benchmarkManager.getBenchmarksByGroup(groupTwo.getId());

        assertEquals(EXPECTED_SINGLE, benchmarks.size());
        assertEquals(benchmarkTwo, benchmarks.iterator().next());
    }
}
