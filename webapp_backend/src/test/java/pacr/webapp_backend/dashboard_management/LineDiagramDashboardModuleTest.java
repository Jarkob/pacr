package pacr.webapp_backend.dashboard_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class LineDiagramDashboardModuleTest {

    private static LineDiagramDashboardModule lineDiagramModule;

    @BeforeEach
    void init() {
        lineDiagramModule = new LineDiagramDashboardModule();
    }

    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        final QueueDashboardModule queueModule = new QueueDashboardModule();

        assertNotEquals(lineDiagramModule, queueModule);
    }

    @Test
    void equals_DifferentAmountOfRepositories_ShouldReturnFalse() {
        final LineDiagramDashboardModule otherLineDiagramModule = new LineDiagramDashboardModule();

        lineDiagramModule.setTrackedRepositories(Arrays.asList("test_repo"));
        otherLineDiagramModule.setTrackedRepositories(Arrays.asList("test_repo", "test_repo_2"));

        assertNotEquals(lineDiagramModule, otherLineDiagramModule);
    }

    @Test
    void equals_DifferentRepositories_ShouldReturnFalse() {
        final LineDiagramDashboardModule otherLineDiagramModule = new LineDiagramDashboardModule();

        lineDiagramModule.setTrackedRepositories(Arrays.asList("test_repo"));
        otherLineDiagramModule.setTrackedRepositories(Arrays.asList("test_repo_different"));

        assertNotEquals(lineDiagramModule, otherLineDiagramModule);
    }

    @Test
    void equals_DifferentAmountOfBenchmarks_ShouldReturnFalse() {
        final LineDiagramDashboardModule otherLineDiagramModule = new LineDiagramDashboardModule();

        lineDiagramModule.setTrackedBenchmarks(Arrays.asList("test_benchmark"));
        otherLineDiagramModule.setTrackedBenchmarks(Arrays.asList("test_benchmark", "test_benchmark_2"));

        assertNotEquals(lineDiagramModule, otherLineDiagramModule);
    }

    @Test
    void equals_DifferentBenchmarks_ShouldReturnFalse() {
        final LineDiagramDashboardModule otherLineDiagramModule = new LineDiagramDashboardModule();

        lineDiagramModule.setTrackedBenchmarks(Arrays.asList("test_benchmark"));
        otherLineDiagramModule.setTrackedBenchmarks(Arrays.asList("test_benchmark_other"));

        assertNotEquals(lineDiagramModule, otherLineDiagramModule);
    }

    @Test
    void equals_SameRepositoriesAndBenchmarks_ShouldReturnTrue() {
        final String BENCHMARK_NAME = "a benchmark";
        final String REPOSITORY_NAME = "a repository";

        final LineDiagramDashboardModule otherLineDiagramModule = new LineDiagramDashboardModule();

        lineDiagramModule.setTrackedBenchmarks(Arrays.asList(BENCHMARK_NAME));
        otherLineDiagramModule.setTrackedBenchmarks(Arrays.asList(BENCHMARK_NAME));

        lineDiagramModule.setTrackedRepositories(Arrays.asList(REPOSITORY_NAME));
        otherLineDiagramModule.setTrackedRepositories(Arrays.asList(REPOSITORY_NAME));

        assertEquals(lineDiagramModule, otherLineDiagramModule);
    }
}
