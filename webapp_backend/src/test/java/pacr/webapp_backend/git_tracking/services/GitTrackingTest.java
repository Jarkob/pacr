package pacr.webapp_backend.git_tracking.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.git.GitHandler;
import pacr.webapp_backend.git_tracking.services.git.PullFromRepositoryException;
import pacr.webapp_backend.shared.ICommitBenchmarkedChecker;
import pacr.webapp_backend.shared.IJobScheduler;
import pacr.webapp_backend.shared.IResultDeleter;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test cases for GitTracking.
 *
 * @author Pavel Zwerschke
 */
public class GitTrackingTest {

    private GitTracking gitTracking;
    @Mock
    private IGitTrackingAccess gitTrackingAccess;
    @Mock
    private GitHandler gitHandler;
    @Mock
    private IResultDeleter resultDeleter;
    @Mock
    private IJobScheduler jobScheduler;
    @Mock
    private IColorPicker colorPicker;
    @Mock
    private ICommitBenchmarkedChecker commitBenchmarkedChecker;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        gitTracking = new GitTracking(gitTrackingAccess, gitHandler, resultDeleter,
                jobScheduler, colorPicker, commitBenchmarkedChecker);
    }

    @Test
    public void addRepository() {
        int repositoryId = 10;
        when(gitTrackingAccess.addRepository(any())).thenReturn(repositoryId);
        when(colorPicker.getNextColor()).thenReturn("#000000");

        ArgumentCaptor<GitRepository> repositoryCaptor = ArgumentCaptor.forClass(GitRepository.class);

        assertEquals(repositoryId,
                gitTracking.addRepository("git@git.scc.kit.edu:pacr/pacr.git", null,
                        "name", new HashSet<>(Arrays.asList("testBranch1")), false, false));

        verify(gitTrackingAccess).addRepository(repositoryCaptor.capture());
        assertEquals("https://git.scc.kit.edu/pacr/pacr/commit/",
                repositoryCaptor.getValue().getCommitLinkPrefix());
        verify(colorPicker).getNextColor();

    }

    @Test
    public void getAllRepositories() {
        GitRepository repository = Mockito.mock(GitRepository.class);
        when(gitTrackingAccess.getAllRepositories()).thenReturn(Arrays.asList(repository));

        Collection<GitRepository> repositories = gitTracking.getAllRepositories();
        assertEquals(1, repositories.size());
        assertEquals(repository, repositories.iterator().next());

        verify(gitTrackingAccess).getAllRepositories();
    }

    @Test
    public void removeRepository() {
        int repositoryId = 42;

        GitCommit commit1 = Mockito.mock(GitCommit.class);
        GitCommit commit2 = Mockito.mock(GitCommit.class);
        when(commit1.getCommitHash()).thenReturn("hash1");
        when(commit2.getCommitHash()).thenReturn("hash2");

        GitRepository repository = Mockito.mock(GitRepository.class);

        when(gitTrackingAccess.getRepository(repositoryId)).thenReturn(repository);
        when(gitTrackingAccess.getAllCommits(repositoryId)).thenReturn(Arrays.asList(commit1, commit2));

        gitTracking.removeRepository(repositoryId);

        verify(resultDeleter).deleteBenchmarkingResults(anyCollection());

        verify(gitTrackingAccess).removeRepository(repositoryId);
    }

    @Test
    public void removeRepositoryNotFound() {
        assertThrows(NoSuchElementException.class, () -> gitTracking.removeRepository(42));
    }

    @Test
    public void updateRepository() {
        GitRepository repository = Mockito.mock(GitRepository.class);

        gitTracking.updateRepository(repository);

        verify(gitTrackingAccess).updateRepository(repository);
    }

    @Test
    public void pullFromRepository() throws PullFromRepositoryException {
        GitRepository repository = Mockito.mock(GitRepository.class);
        when(repository.getPullURL()).thenReturn("pull url");
        when(gitTrackingAccess.getRepository(anyInt())).thenReturn(repository);

        String hash1 = "hash 1";
        String hash2 = "hash 2";

        when(gitHandler.pullFromRepository(repository)).thenReturn(new HashSet<>(Arrays.asList(hash1, hash2)));

        gitTracking.pullFromRepository(42);

        verify(gitHandler).pullFromRepository(repository);

        // TODO: verify that jobs were added
    }

    @Test
    public void pullFromRepositoryNotFound() {
        when(gitTrackingAccess.getRepository(anyInt())).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> gitTracking.pullFromRepository(42));
    }

    @Test
    public void pullFromAllRepositories() throws PullFromRepositoryException {
        GitRepository repository1 = mock(GitRepository.class);
        when(repository1.isHookSet()).thenReturn(true);
        when(repository1.getId()).thenReturn(1);

        GitRepository repository2 = mock(GitRepository.class);
        when(repository2.isHookSet()).thenReturn(false);
        when(repository2.getId()).thenReturn(2);

        when(gitTrackingAccess.getAllRepositories()).thenReturn(Arrays.asList(repository1, repository2));
        when(gitTrackingAccess.getRepository(1)).thenReturn(repository1);
        when(gitTrackingAccess.getRepository(2)).thenReturn(repository2);
        when(gitHandler.pullFromRepository(any())).thenReturn(new HashSet<>());

        gitTracking.pullFromAllRepositories();

        verify(gitTrackingAccess).getAllRepositories();
        // one time for setting branches, one time for getting new commits
        verify(gitTrackingAccess, times(2)).getRepository(2);
        verify(gitTrackingAccess, times(2)).updateRepository(repository2);

        verify(gitHandler).setBranchesToRepo(repository2);
        verifyNoMoreInteractions(gitTrackingAccess);
    }


}
