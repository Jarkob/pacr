package pacr.webapp_backend.git_tracking.services.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import pacr.webapp_backend.git_tracking.endpoints.RepositoryManagerController;
import pacr.webapp_backend.git_tracking.endpoints.TransferRepository;
import pacr.webapp_backend.git_tracking.services.GitTracking;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.shared.IAuthenticator;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test cases for RepositoryManagerController.
 *
 * @author Pavel Zwerschke
 */
public class RepositoryManagerControllerTest {

    private RepositoryManagerController repositoryManagerController;

    @Mock
    private GitTracking gitTracking;
    @Mock
    private IAuthenticator authenticator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        repositoryManagerController = new RepositoryManagerController(gitTracking, authenticator);
    }

    @Test
    public void getAllRepositories() {

        GitRepository gitRepository = mock(GitRepository.class);

        when(gitTracking.getAllRepositories()).thenReturn(Collections.singletonList(gitRepository));
        when(gitRepository.getTrackedBranchNames()).thenReturn(new HashSet<>(Collections.singletonList("master")));

        List<TransferRepository> repositories = repositoryManagerController.getAllRepositories();

        assertEquals(1, repositories.size());

        TransferRepository repository = repositories.get(0);
        assertEquals(gitRepository.getName(), repository.getName());
        assertEquals(gitRepository.getPullURL(), repository.getPullURL());
        assertEquals(gitRepository.getColor(), repository.getColor());
        assertEquals(gitRepository.getCommitLinkPrefix(), repository.getCommitLinkPrefix());
        List<String> trackedBranches = repository.getTrackedBranches();
        assertEquals(1, trackedBranches.size());
        assertEquals("master", trackedBranches.get(0));
        assertEquals("/webhooks/" + gitRepository.getId(), repository.getWebHookURL());
    }

    @Test
    public void getAllCommits() {
        Pageable pageable = mock(Pageable.class);
        int id = 5;
        repositoryManagerController.getAllCommits(pageable, id);
        verify(gitTracking).getAllCommits(id, pageable);
    }

    @Test
    public void addRepository() {
        when(authenticator.authenticate(anyString())).thenReturn(true);

        TransferRepository repository = mock(TransferRepository.class);
        when(repository.getName()).thenReturn("NAME");
        when(repository.getPullURL()).thenReturn("pullURL");
        when(repository.isTrackAllBranches()).thenReturn(true);
        when(repository.isHookSet()).thenReturn(false);

        when(repository.getTrackedBranches()).thenReturn(Arrays.asList("master", "desaster"));
        when(gitTracking.getBranches(repository.getPullURL()))
                .thenReturn(new HashSet<>(Arrays.asList("master", "desaster", "testbranch1")));

        when(gitTracking.addRepository(anyString(), any(), anyString(), anySet(), anyBoolean(), anyBoolean()))
                .thenReturn(5);

        assertEquals(5, repositoryManagerController.addRepository(repository, ""));

        verify(gitTracking).addRepository(anyString(), any(), anyString(),
                anySet(), anyBoolean(), anyBoolean());
    }

    @Test
    public void authenticateError() {
        when(authenticator.authenticate(anyString())).thenReturn(false);

        assertThrows(ResponseStatusException.class,
                () -> repositoryManagerController.addRepository(mock(TransferRepository.class), ""));

        assertThrows(ResponseStatusException.class,
                () -> repositoryManagerController.deleteRepository(5, ""));

        assertThrows(ResponseStatusException.class,
                () -> repositoryManagerController.updateRepository(mock(TransferRepository.class), ""));
    }

    @Test
    public void updateRepository() {
        when(authenticator.authenticate(anyString())).thenReturn(true);

        TransferRepository repository = mock(TransferRepository.class);
        GitRepository gitRepository = mock(GitRepository.class);

        when(repository.getId()).thenReturn(5);

        when(gitTracking.getRepository(repository.getId())).thenReturn(gitRepository);

        when(gitRepository.getColor()).thenReturn("#000000");
        when(repository.getColor()).thenReturn("#ffffff");

        when(gitRepository.getObserveFromDate()).thenReturn(null);
        when(repository.getObserveFromDate()).thenReturn(LocalDate.now());

        when(repository.isTrackAllBranches()).thenReturn(false);

        repositoryManagerController.updateRepository(repository, "");

        verify(gitTracking).updateColorOfRepository(gitRepository, repository.getColor());
        verify(gitTracking).updateObserveFromDateOfRepository(gitRepository, repository.getObserveFromDate());

        verify(gitTracking).updateRepository(gitRepository);
    }

    @Test
    public void getBranches() {
        when(gitTracking.getBranches("pullurl")).thenReturn(new HashSet<>(Arrays.asList("master", "branch")));

        List<String> branchNames = repositoryManagerController.getBranchesFromRepository("pullurl");

        assertEquals(2, branchNames.size());
        assertEquals("branch", branchNames.get(0));
        assertEquals("master", branchNames.get(1));
    }

}
