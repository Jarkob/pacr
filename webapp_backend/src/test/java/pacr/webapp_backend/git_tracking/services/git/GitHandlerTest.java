package pacr.webapp_backend.git_tracking.services.git;

import javassist.NotFoundException;
import net.lingala.zip4j.exception.ZipException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.git_tracking.GitBranch;
import pacr.webapp_backend.git_tracking.GitCommit;
import pacr.webapp_backend.git_tracking.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;

import java.io.File;
import java.io.IOException;
import java.util.*;

import net.lingala.zip4j.core.ZipFile;
import pacr.webapp_backend.shared.IResultDeleter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test cases for GitHandler.
 * Internet connection required.
 *
 * @author Pavel Zwerschke
 */
public class GitHandlerTest {

    private static final String PATH_TO_REPOS = "/target/test/repos";
    private static final String ABSOLUTE_PATH_TO_REPOS = System.getProperty("user.dir") + PATH_TO_REPOS;
    private static final String RESOURCES = System.getProperty("user.dir")
            + "/src/test/resources/pacr/webapp_backend/git_tracking/services/git";
    private static final String RSA = RESOURCES + "/id_rsa";
    private static final String DSA = RESOURCES + "/id_dsa";
    private static final String ECDSA = RESOURCES + "/id_ecdsa";
    private static final String ED25519 = RESOURCES + "/id_ed25519";
    private static final String PULL_URL = "git@git.scc.kit.edu:pacr/pacr-test-repository.git";
    private static final String PULL_URL_NO_AUTHORIZATION = "git@git.scc.kit.edu:pacr/pacr-specification.git";
    private static final String NEW_COMMITS_REPOSITORY = RESOURCES + "/newCommits.zip";
    private static final String FORCE_PUSH_REPOSITORY = RESOURCES + "/forcePush.zip";

    private static final String HASH_39E1A8 = "39e1a8c8f9951015a101c18c55533947d0a44bdd";
    private static final String HASH_9C8C86 = "9c8c86f5939c88329d9f46f7f5266f6c6b2e515e";
    private static final String HASH_E68151 = "e68151d6e1031609238c0a12ecbea8ce478b0c70";
    private static final String HASH_E4E234 = "e4e234247dbb8f18c77c9c8678788735e15b7fcb";
    private static final String HASH_08AF11 = "08af11060c72caa7168bbf5fb4f59cc432dcbc96";
    private static final String HASH_AB1008 = "ab1008e07bfe9c84b9cc994eb02e5e5bb241a98f";
    private static final String HASH_6FDE0D = "6fde0d353d758700e03f95885c079b2cfafbf00f";
    private static final String HASH_AC7821 = "ac782173736902511a6f3214e0cac3068b27a448";
    private static final String HASH_AC1E8B = "ac1e8bcda08057edba84469ec80f69a566de58e4";
    private static final String HASH_8926F7 = "8926f7e91d42eb8b5b88343694bd6cd311d6e180";

    private static int repositoryId = 0;

    private GitHandler gitHandler;
    @Mock
    private GitRepository gitRepository;
    @Mock
    private GitBranch masterBranch;
    @Mock
    private GitBranch testBranch1;
    @Mock
    private GitBranch testBranch2;
    @Mock
    private IGitTrackingAccess gitTrackingAccess;
    @Mock
    private ICleanUpCommits cleanUpCommits;
    @Mock
    private IResultDeleter resultDeleter;

    @BeforeAll
    public static void deleteFolders() {
        File repos = new File(ABSOLUTE_PATH_TO_REPOS);
        if (repos.exists()) {
            try {
                FileUtils.deleteDirectory(repos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterAll
    public static void cleanUp() {
        deleteFolders();
    }

    @BeforeEach
    public void setUp() throws NotFoundException {
        MockitoAnnotations.initMocks(this);

        newRepositoryId(repositoryId + 1);

        when(gitRepository.getPullURL()).thenReturn(PULL_URL);
        when(gitRepository.getId()).thenReturn(repositoryId);

        // branches
        String MASTER = "master";
        String TEST_BRANCH1 = "testbranch1";
        String TEST_BRANCH2 = "testbranch2";
        when(masterBranch.getName()).thenReturn(MASTER);
        when(testBranch1.getName()).thenReturn(TEST_BRANCH1);
        when(testBranch2.getName()).thenReturn(TEST_BRANCH2);
        when(gitRepository.getSelectedBranch(MASTER)).thenReturn(masterBranch);
        when(gitRepository.getSelectedBranch(TEST_BRANCH1)).thenReturn(testBranch1);
        when(gitRepository.getSelectedBranch(TEST_BRANCH2)).thenReturn(testBranch2);
        when(gitRepository.getSelectedBranches()).thenReturn(Arrays.asList(masterBranch, testBranch1));

        // tracked branches
        when(gitRepository.isBranchSelected(anyString())).thenReturn(false);
        when(gitRepository.isBranchSelected(masterBranch.getName())).thenReturn(true);
        when(gitRepository.isBranchSelected(testBranch1.getName())).thenReturn(true);
        when(gitRepository.getSelectedBranches()).thenReturn(Arrays.asList(masterBranch, testBranch1));

        // by default no commits are already being tracked
        when(gitTrackingAccess.containsCommit(anyString())).thenReturn(false);
    }

    /**
     * Clones a repository with a RSA ssh key.
     */
    @Test
    public void cloneRepositoryRSA() throws GitAPIException {
        initializeGitHandler(RSA);

        cloneRepository();
    }

    /**
     * Clones a repository with a DSA ssh key.
     */
    @Test @Disabled
    public void cloneRepositoryDSA() throws GitAPIException {
        initializeGitHandler(DSA);

        cloneRepository();
    }

    /**
     * Clones a repository with a ECDSA ssh key.
     */
    @Test @Disabled
    public void cloneRepositoryECDSA() throws GitAPIException {
        initializeGitHandler(ECDSA);

        cloneRepository();
    }

    /**
     * Clones a repository with a ED25519 ssh key.
     */
    @Test @Disabled
    public void cloneRepositoryED25519() throws GitAPIException {
        initializeGitHandler(ED25519);

        cloneRepository();
    }

    private void cloneRepository() throws GitAPIException {
        gitHandler.cloneRepository(gitRepository);
        File fileInRepository = new File(ABSOLUTE_PATH_TO_REPOS + "/" + repositoryId + "/README.md");
        assertTrue(fileInRepository.exists());
    }

    /**
     * Tries to clone a private repository,
     * but the SSH Public Key is not saved in the repository.
     */
    @Test
    public void cloneRepositoryNoAuthorization() {
        initializeGitHandler(RSA);

        when(gitRepository.getPullURL()).thenReturn(PULL_URL_NO_AUTHORIZATION);

        assertThrows(GitAPIException.class, () -> gitHandler.cloneRepository(gitRepository));
    }

    @Test
    public void updateNewRepository() {
        initializeGitHandler(RSA);

        Collection<GitCommit> untrackedCommits = null;
        try {
            untrackedCommits = gitHandler.updateRepository(gitRepository);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            fail();
        }

        verify(gitRepository).isBranchSelected(masterBranch.getName());
        verify(gitRepository).isBranchSelected(testBranch1.getName());
        verify(gitRepository).isBranchSelected(testBranch2.getName());

        assertNotNull(untrackedCommits);
        assertEquals(8, untrackedCommits.size());

        assertCommit(getCommitWithHash(HASH_E68151, untrackedCommits),
                1, Arrays.asList(masterBranch, testBranch1));
        assertEquals("Add new file",
                Objects.requireNonNull(getCommitWithHash(HASH_E68151, untrackedCommits)).getMessage());
        assertCommit(getCommitWithHash(HASH_9C8C86, untrackedCommits),
                1, Arrays.asList(masterBranch, testBranch1));
        // first commit
        assertCommit(getCommitWithHash(HASH_39E1A8, untrackedCommits),
                0, Arrays.asList(masterBranch, testBranch1));
        // merge commit
        assertCommit(getCommitWithHash(HASH_AC7821, untrackedCommits),
                2, Arrays.asList(masterBranch));
        // commit is on testbranch2 and was merged into master, so it should be tracked
        assertCommit(getCommitWithHash(HASH_E4E234, untrackedCommits),
                1, Arrays.asList(masterBranch));
        // commit is on testbranch2 and should not be tracked
        assertNull(getCommitWithHash(HASH_AC1E8B, untrackedCommits));
    }

    @Test
    public void updateRepositoryWithNewCommits() throws NotFoundException {
        // repository is already cloned
        unzip(NEW_COMMITS_REPOSITORY, ABSOLUTE_PATH_TO_REPOS);
        initializeGitHandler(RSA);
        newRepositoryId(19);


        // commits until e68151 already tracked
        GitCommit tracked1 = initializeCommitMock(HASH_39E1A8);
        GitCommit tracked2 = initializeCommitMock(HASH_9C8C86);
        GitCommit tracked3 = initializeCommitMock(HASH_E68151);

        when(gitTrackingAccess.getHead(masterBranch.getName())).thenReturn(tracked3);
        when(gitTrackingAccess.getHead(testBranch1.getName())).thenReturn(null);

        Collection<GitCommit> untrackedCommits = null;
        try {
            untrackedCommits = gitHandler.updateRepository(gitRepository);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            fail();
        }

        verify(testBranch1).addCommit(tracked1);
        verify(testBranch1).addCommit(tracked2);
        verify(testBranch1).addCommit(tracked3);
        verify(gitTrackingAccess).updateRepository(gitRepository);

        assertNotNull(untrackedCommits);
        assertEquals(5, untrackedCommits.size());

        assertCommit(getCommitWithHash(HASH_AC7821, untrackedCommits),
                2, Arrays.asList(masterBranch));
        assertCommit(getCommitWithHash(HASH_6FDE0D, untrackedCommits),
                1, Arrays.asList(masterBranch, testBranch1));
        assertCommit(getCommitWithHash(HASH_AB1008, untrackedCommits),
                2, Arrays.asList(masterBranch));
        assertCommit(getCommitWithHash(HASH_08AF11, untrackedCommits),
                1, Arrays.asList(masterBranch));
        assertCommit(getCommitWithHash(HASH_E4E234, untrackedCommits),
                1, Arrays.asList(masterBranch));
    }

    @Test
    public void updateRepositoryForcePush() {
        // repository is already cloned
        unzip(FORCE_PUSH_REPOSITORY, ABSOLUTE_PATH_TO_REPOS);
        initializeGitHandler(RSA);
        newRepositoryId(39);

        // commits until 9c8c86 already tracked and commit 8926f7 is not on origin anymore
        initializeCommitMock(HASH_39E1A8);
        GitCommit commitBeforeHead = initializeCommitMock(HASH_9C8C86);
        GitCommit head = initializeCommitMock(HASH_8926F7);

        when(gitTrackingAccess.getHead(masterBranch.getName())).thenReturn(head).thenReturn(commitBeforeHead);
        when(gitTrackingAccess.getHead(testBranch1.getName())).thenReturn(null);

        when(gitTrackingAccess.containsCommit(HASH_8926F7)).thenReturn(true).thenReturn(false);

        when(gitTrackingAccess.getAllCommitHashes(repositoryId)).thenReturn(Arrays.asList(
                HASH_39E1A8,
                HASH_9C8C86,
                HASH_8926F7));

        when(cleanUpCommits.cleanUp(any(), eq(gitRepository))).thenReturn(Arrays.asList(HASH_8926F7));

        Collection<GitCommit> untrackedCommits = null;
        try {
            untrackedCommits = gitHandler.updateRepository(gitRepository);
        } catch (GitAPIException | IOException e) {
            e.printStackTrace();
            fail();
        }
        assertNotNull(untrackedCommits);

        verify(cleanUpCommits).cleanUp(any(), eq(gitRepository));
        verify(resultDeleter).deleteBenchmarkingResults(HASH_8926F7);

    }

    private void newRepositoryId(int id) {
        when(gitRepository.getId()).thenReturn(id);
        repositoryId = id;
    }

    private void assertCommit(GitCommit commit, int parentCount, Collection<GitBranch> branches) {
        assertNotNull(commit);
        assertNotNull(commit.getCommitHash());
        assertNotNull(commit.getCommitDate());
        assertNotNull(commit.getAuthorDate());
        assertNotNull(commit.getEntryDate());
        assertNotNull(commit.getMessage());
        assertNotNull(commit.getLabels());

        assertEquals(repositoryId, commit.getRepositoryID());
        assertEquals(parentCount, commit.getParents().size());
        assertCollectionEquals(branches, commit.getBranches());
    }

    private void assertCollectionEquals(Collection<?> expected, Collection<?> actual) {
        assertEquals(expected.size(), actual.size());
        for (Object o : expected) {
            assertTrue(actual.contains(o));
        }
    }

    private void unzip(String zip, String destination) {
        try {
            ZipFile zipFile = new ZipFile(zip);
            zipFile.extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
            fail();
        }
    }

    private GitCommit getCommitWithHash(String startsWith, Collection<GitCommit> commits) {
        for (GitCommit commit : commits) {
            if (commit.getCommitHash().startsWith(startsWith)) {
                return commit;
            }
        }
        return null;
    }

    private void initializeGitHandler(String pathToPrivateKey) {
        TransportConfigCallback transportConfigCallback
                = new SSHTransportConfigCallback(pathToPrivateKey);

        // create GitHandler
        try {
            gitHandler = new GitHandler(
                    PATH_TO_REPOS, transportConfigCallback, gitTrackingAccess, cleanUpCommits, resultDeleter);
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    private GitCommit initializeCommitMock(String commitHash) {
        when(gitTrackingAccess.containsCommit(commitHash)).thenReturn(true);
        GitCommit commit = Mockito.mock(GitCommit.class);
        when(commit.getCommitHash()).thenReturn(commitHash);
        when(gitTrackingAccess.getCommit(commitHash)).thenReturn(commit);

        return commit;
    }
}
