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

        commitHistoryModule = new CommitHistoryDashboardModule();
    }


    @Test
    void equals_DifferentClass_ShouldReturnFalse() {
        QueueDashboardModule queueModule = new QueueDashboardModule();

        assertNotEquals(commitHistoryModule, queueModule);
    }

    @Test
    void equals_DifferentAmountOfRepositories_ShouldReturnFalse() {
        CommitHistoryDashboardModule otherCommitHistoryModule = new CommitHistoryDashboardModule();

        commitHistoryModule.setTrackedRepositories(Arrays.asList("testRepository"));
        otherCommitHistoryModule.setTrackedRepositories(Arrays.asList("testRepository", "testRepository 2"));

        assertNotEquals(commitHistoryModule, otherCommitHistoryModule);
    }


    @Test
    void equals_DifferentRepositories_ShouldReturnFalse() {
        CommitHistoryDashboardModule otherCommitHistoryModule = new CommitHistoryDashboardModule();

        commitHistoryModule.setTrackedRepositories(Arrays.asList("testRepository"));
        otherCommitHistoryModule.setTrackedRepositories(Arrays.asList("testRepository 2"));

        assertNotEquals(commitHistoryModule, otherCommitHistoryModule);
    }

    @Test
    void equals_SameRepositories_ShouldReturnTrue() {
        final String REPOSITORY_NAME = "test repository";

        CommitHistoryDashboardModule otherCommitHistoryModule = new CommitHistoryDashboardModule();

        commitHistoryModule.setTrackedRepositories(Arrays.asList(REPOSITORY_NAME));
        otherCommitHistoryModule.setTrackedRepositories(Arrays.asList(REPOSITORY_NAME));

        assertEquals(commitHistoryModule, otherCommitHistoryModule);
    }
}
