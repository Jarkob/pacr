package pacr.webapp_backend.dashboard_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class LineDiagramDashboardModuleTest {

    private static LineDiagramDashboardModule lineDiagramModule;

    @BeforeEach
    void init() {
        lineDiagramModule = new LineDiagramDashboardModule(6);
    }

    @Test
    void constructor_NoArguments_ShouldHaveInvalidState() {
        LineDiagramDashboardModule lineDiagramModule = new LineDiagramDashboardModule();

        assertThrows(IllegalStateException.class, lineDiagramModule::getPosition);
    }

    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        QueueDashboardModule queueModule = new QueueDashboardModule(6);

        assertNotEquals(lineDiagramModule, queueModule);
    }

    @Test
    void equals_DifferentPosition_ShouldReturnFalse() {
        LineDiagramDashboardModule otherLineDiagramModule = new LineDiagramDashboardModule(2);

        assertNotEquals(lineDiagramModule, otherLineDiagramModule);
    }

    @Test
    void equals_DifferentRepositories_ShouldReturnFalse() {
        LineDiagramDashboardModule otherLineDiagramModule = new LineDiagramDashboardModule(6);

        lineDiagramModule.setTrackedRepositories(Arrays.asList("test_repo"));
        otherLineDiagramModule.setTrackedRepositories(Arrays.asList("test_repo_different"));

        assertNotEquals(lineDiagramModule, otherLineDiagramModule);
    }

    @Test
    void equals_DifferentBenchmarks_ShouldReturnFalse() {
        LineDiagramDashboardModule otherLineDiagramModule = new LineDiagramDashboardModule(6);

        lineDiagramModule.setTrackedBenchmarks(Arrays.asList("test_benchmark"));
        otherLineDiagramModule.setTrackedBenchmarks(Arrays.asList("test_benchmark_other"));

        assertNotEquals(lineDiagramModule, otherLineDiagramModule);
    }

    @Test
    void equals_SameRepositoriesAndBenchmarks_ShouldReturnTrue() {
        final String BENCHMARK_NAME = "a benchmark";
        final String REPOSITORY_NAME = "a repository";

        LineDiagramDashboardModule otherLineDiagramModule = new LineDiagramDashboardModule(6);

        lineDiagramModule.setTrackedBenchmarks(Arrays.asList(BENCHMARK_NAME));
        otherLineDiagramModule.setTrackedBenchmarks(Arrays.asList(BENCHMARK_NAME));

        lineDiagramModule.setTrackedRepositories(Arrays.asList(REPOSITORY_NAME));
        otherLineDiagramModule.setTrackedRepositories(Arrays.asList(REPOSITORY_NAME));

        assertEquals(lineDiagramModule, otherLineDiagramModule);
    }
}
