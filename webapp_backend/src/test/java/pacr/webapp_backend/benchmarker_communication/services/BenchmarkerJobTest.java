package pacr.webapp_backend.benchmarker_communication.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BenchmarkerJobTest {

    private static final String ADDRESS = "address";
    private static final String REPO_URL = "repoURL";
    private static final String COMMIT_HASH = "commitHash";

    private BenchmarkerJob benchmarkerJob;

    @BeforeEach
    void setUp() {
        this.benchmarkerJob = new BenchmarkerJob(ADDRESS, REPO_URL, COMMIT_HASH);
    }

    @Test
    void BenchmarkerJob_noArgs() {
        assertDoesNotThrow(() -> {
           BenchmarkerJob benchmarkerJob = new BenchmarkerJob();
        });
    }

    @Test
    void BenchmarkerJob_withArgs_noError() {
        assertDoesNotThrow(() -> {
            BenchmarkerJob benchmarkerJob = new BenchmarkerJob(ADDRESS, REPO_URL, COMMIT_HASH);
        });
    }

    @Test
    void BenchmarkerJob_withArgs_withError() {
        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkerJob benchmarkerJob = new BenchmarkerJob(null, REPO_URL, COMMIT_HASH);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkerJob benchmarkerJob = new BenchmarkerJob("", REPO_URL, COMMIT_HASH);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkerJob benchmarkerJob = new BenchmarkerJob(" ", REPO_URL, COMMIT_HASH);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkerJob benchmarkerJob = new BenchmarkerJob(ADDRESS, null, COMMIT_HASH);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkerJob benchmarkerJob = new BenchmarkerJob(ADDRESS, "", COMMIT_HASH);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkerJob benchmarkerJob = new BenchmarkerJob(ADDRESS, " ", COMMIT_HASH);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkerJob benchmarkerJob = new BenchmarkerJob(ADDRESS, REPO_URL, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkerJob benchmarkerJob = new BenchmarkerJob(ADDRESS, REPO_URL, "");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            BenchmarkerJob benchmarkerJob = new BenchmarkerJob(ADDRESS, REPO_URL, " ");
        });
    }

    @Test
    void getAddress_noError() {
        assertEquals(ADDRESS, benchmarkerJob.getAddress());
    }

    @Test
    void getCommitHash_noError() {
        assertEquals(COMMIT_HASH, benchmarkerJob.getCommitHash());
    }

    @Test
    void getRepository_noError() {
        assertEquals(REPO_URL, benchmarkerJob.getRepository());
    }
}
