package pacr.webapp_backend.dashboard_management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommitHistoryDashboardModuleTest {

    private CommitHistoryDashboardModule commitHistoryModule;


    @BeforeEach
    void init() {

        commitHistoryModule = new CommitHistoryDashboardModule(4);
    }


    @Test
    void constructor_NoArguments_ShouldHaveInvalidState() {
        CommitHistoryDashboardModule chdm = new CommitHistoryDashboardModule();

        assertThrows(IllegalStateException.class, chdm::getPosition);
    }

    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        QueueDashboardModule queueModule = new QueueDashboardModule(4);

        assertNotEquals(commitHistoryModule, queueModule);
    }

    @Test
    void equals_DifferentPosition_ShouldReturnFalse() {
        CommitHistoryDashboardModule otherCommitHistoryModule = new CommitHistoryDashboardModule(7);

        assertNotEquals(commitHistoryModule, otherCommitHistoryModule);
    }

    @Test
    void equals_DifferentRepositories_ShouldReturnFalse() {
        CommitHistoryDashboardModule otherCommitHistoryModule = new CommitHistoryDashboardModule(4);

        commitHistoryModule.setTrackedRepositories(Arrays.asList("testRepository"));
        otherCommitHistoryModule.setTrackedRepositories(Arrays.asList("testRepository 2"));

        assertNotEquals(commitHistoryModule, otherCommitHistoryModule);
    }

    @Test
    void equals_SameRepositories_ShouldReturnTrue() {
        final String REPOSITORY_NAME = "test repository";

        CommitHistoryDashboardModule otherCommitHistoryModule = new CommitHistoryDashboardModule(4);

        commitHistoryModule.setTrackedRepositories(Arrays.asList(REPOSITORY_NAME));
        otherCommitHistoryModule.setTrackedRepositories(Arrays.asList(REPOSITORY_NAME));

        assertEquals(commitHistoryModule, otherCommitHistoryModule);
    }
}
