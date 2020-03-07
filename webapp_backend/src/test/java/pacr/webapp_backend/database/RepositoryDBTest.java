package pacr.webapp_backend.database;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.IRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RepositoryDBTest extends SpringBootTestWithoutShell {

    private static final String REPO_NAME = "repoName";
    private static final String PULL_URL = "pullUrl";
    private static final Set<String> TRACKED_BRANCHES = Set.of("branch1", "branch2");

    private RepositoryDB repositoryDB;

    private GitRepository expectedRepository;

    @Autowired
    public RepositoryDBTest(RepositoryDB repositoryDB) {
        this.expectedRepository = new GitRepository();
        this.expectedRepository.setName(REPO_NAME);
        this.expectedRepository.setSelectedBranches(TRACKED_BRANCHES);
        this.expectedRepository.setPullURL(PULL_URL);

        this.repositoryDB = repositoryDB;
        this.repositoryDB.deleteAll();
    }

    @AfterEach
    void setUp() {
        this.repositoryDB.deleteAll();
    }

    @Test
    void findGitRepositoryById_noError() {
        int id = repositoryDB.save(expectedRepository).getId();

        IRepository repository = repositoryDB.findGitRepositoryById(id);

        assertEquals(id, expectedRepository.getId());
        checkRepository(repository, expectedRepository.getName(),
                expectedRepository.getPullURL(), expectedRepository.getTrackedBranchNames());
    }

    @Test
    void findAllByOrderByName_noError() {
        GitRepository expectedRepository1 = new GitRepository();
        expectedRepository1.setName(REPO_NAME + 1);
        expectedRepository1.setSelectedBranches(TRACKED_BRANCHES);
        expectedRepository1.setPullURL(PULL_URL);

        repositoryDB.save(expectedRepository);
        repositoryDB.save(expectedRepository1);

        List<GitRepository> repositories = repositoryDB.findAllByOrderByName();

        assertNotNull(repositories);
        assertEquals(2, repositories.size());

        checkRepository(repositories.get(0), expectedRepository.getName(), expectedRepository.getPullURL(),
                expectedRepository.getTrackedBranchNames());

        checkRepository(repositories.get(1), expectedRepository1.getName(), expectedRepository1.getPullURL(),
                expectedRepository1.getTrackedBranchNames());
    }

    private void checkRepository(IRepository repository, String name, String pullUrl, Set<String> trackedBranches) {
        assertEquals(name, repository.getName());
        assertEquals(pullUrl, repository.getPullURL());
        assertEquals(trackedBranches, repository.getTrackedBranchNames());
    }
}
