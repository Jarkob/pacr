package pacr.webapp_backend.git_tracking.services.git;

import net.lingala.zip4j.exception.ZipException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import net.lingala.zip4j.core.ZipFile;
import pacr.webapp_backend.shared.IResultDeleter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for GitHandler.
 * Internet connection required.
 *
 * @author Pavel Zwerschke
 */
public class GitHandlerTest extends SpringBootTestWithoutShell {

    private static final String PATH_TO_REPOS = "/target/test/repos";
    private static final String ABSOLUTE_PATH_TO_REPOS = System.getProperty("user.dir") + PATH_TO_REPOS;
    private static final String RESOURCES = "/src/test/resources/pacr/webapp_backend/git_tracking/services/git";
    private static final String RSA = RESOURCES + "/id_rsa";
    private static final String DSA = RESOURCES + "/id_dsa";
    private static final String ECDSA = RESOURCES + "/id_ecdsa";
    private static final String ED25519 = RESOURCES + "/id_ed25519";
    private static final String PULL_URL = "git@git.scc.kit.edu:pacr/pacr-test-repository.git";
    private static final String PULL_URL_NO_AUTHORIZATION = "git@git.scc.kit.edu:pacr/pacr-specification.git";
    private static final String NEW_COMMITS_REPOSITORY = System.getProperty("user.dir") + RESOURCES + "/newCommits.zip";
    private static final String FORCE_PUSH_REPOSITORY = System.getProperty("user.dir") + RESOURCES + "/forcePush.zip";

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

    private GitHandler gitHandler;
    @Spy
    private GitRepository gitRepository;
    private GitBranch masterBranch;
    private GitBranch testBranch1;
    private GitBranch testBranch2;
    private IGitTrackingAccess gitTrackingAccess;
    @Mock
    private ICleanUpCommits cleanUpCommits;
    @Mock
    private IResultDeleter resultDeleter;

    @Autowired
    public GitHandlerTest(IGitTrackingAccess gitTrackingAccess) {
        this.gitTrackingAccess = gitTrackingAccess;
    }

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

    @AfterEach
    public void cleanDatabase() {
        gitTrackingAccess.removeRepository(gitRepository.getId());
    }

    @AfterAll
    public static void cleanUp() {
        deleteFolders();
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // branches
        String MASTER = "master";
        String TEST_BRANCH1 = "testbranch1";
        String TEST_BRANCH2 = "testbranch2";

        masterBranch = new GitBranch(MASTER);
        testBranch1 = new GitBranch(TEST_BRANCH1);
        testBranch2 = new GitBranch(TEST_BRANCH2);
        Set<GitBranch> branches = new HashSet<>(Arrays.asList(masterBranch, testBranch1));

        gitRepository = Mockito.spy(new GitRepository(false, branches,
                PULL_URL, "testing repo", new Color(0xFFFFFF), null));

        gitTrackingAccess.addRepository(gitRepository);

        initializeGitHandler(RSA);
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

    private GitCommit initializeCommit(String commitHash, Collection<GitBranch> branches) {
        GitCommit commit = new GitCommit(commitHash, "msg", LocalDateTime.now(), LocalDateTime.now(),
                gitRepository);
        for (GitBranch branch : branches) {
            commit.addBranch(branch);
        }
        return commit;
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
     * Disabled because the functionality is not implemented.
     */
    @Test @Disabled
    public void cloneRepositoryDSA() throws GitAPIException {
        initializeGitHandler(DSA);

        cloneRepository();
    }

    /**
     * Clones a repository with a ECDSA ssh key.
     * Disabled because the functionality is not implemented.
     */
    @Test @Disabled
    public void cloneRepositoryECDSA() throws GitAPIException {
        initializeGitHandler(ECDSA);

        cloneRepository();
    }

    /**
     * Clones a repository with a ED25519 ssh key.
     * Disabled because the functionality is not implemented.
     */
    @Test @Disabled
    public void cloneRepositoryED25519() throws GitAPIException {
        initializeGitHandler(ED25519);

        cloneRepository();
    }

    private void cloneRepository() throws GitAPIException {
        gitHandler.cloneRepository(gitRepository);
        File fileInRepository = new File(ABSOLUTE_PATH_TO_REPOS + "/" + gitRepository.getId() + "/README.md");
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

    /**
     * Performs a pull for commits with an uninitialized repository.
     */
    @Test
    public void pullNewRepository() {
/*
        Collection<String> untrackedCommits = gitHandler.pullFromRepository(gitRepository);

        verify(gitRepository).isBranchSelected(masterBranch.getName());
        verify(gitRepository).isBranchSelected(testBranch1.getName());
        verify(gitRepository).isBranchSelected(testBranch2.getName());

        assertNotNull(untrackedCommits);
        assertEquals(8, untrackedCommits.size());

        assertEquals(HASH_AC7821, masterBranch.getLocalHead().getCommitHash());
        assertEquals(HASH_6FDE0D, testBranch1.getLocalHead().getCommitHash());

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
                2, Collections.singletonList(masterBranch));
        // commit is on testbranch2 and was merged into master, so it should be tracked
        assertCommit(getCommitWithHash(HASH_E4E234, untrackedCommits),
                1, Collections.singletonList(masterBranch));
        // commit is on testbranch2 and should not be tracked
        assertNull(getCommitWithHash(HASH_AC1E8B, untrackedCommits));

 */
    }

    /**
     * Performs a pull for commits with a repository that is already initialized.
     */
    @Test
    public void pullRepositoryWithNewCommits() { /*
        // repository is already cloned
        unzip(NEW_COMMITS_REPOSITORY, ABSOLUTE_PATH_TO_REPOS + "/" + gitRepository.getId());

        // commits until e68151 already tracked for testbranch1, until 9c8c86 for master
        initializeCommit(HASH_39E1A8, Arrays.asList(masterBranch, testBranch1));
        GitCommit headMaster = initializeCommit(HASH_9C8C86, Arrays.asList(masterBranch, testBranch1));
        GitCommit headTestBranch1 = initializeCommit(HASH_E68151, Collections.singletonList(testBranch1));

        masterBranch.setLocalHead(headMaster);
        testBranch1.setLocalHead(headTestBranch1);
        gitTrackingAccess.updateRepository(gitRepository);

        Collection<GitCommit> untrackedCommits = gitHandler.pullFromRepository(gitRepository);

        GitCommit updatedTracked3 = gitTrackingAccess.getCommit(HASH_E68151);

        assertEquals(2, updatedTracked3.getBranches().size());

        assertNotNull(untrackedCommits);
        assertEquals(5, untrackedCommits.size());

        assertCommit(getCommitWithHash(HASH_AC7821, untrackedCommits),
                2, Collections.singletonList(masterBranch));
        assertCommit(getCommitWithHash(HASH_6FDE0D, untrackedCommits),
                1, Arrays.asList(masterBranch, testBranch1));
        assertCommit(getCommitWithHash(HASH_AB1008, untrackedCommits),
                2, Collections.singletonList(masterBranch));
        assertCommit(getCommitWithHash(HASH_08AF11, untrackedCommits),
                1, Collections.singletonList(masterBranch));
        assertCommit(getCommitWithHash(HASH_E4E234, untrackedCommits),
                1, Collections.singletonList(masterBranch));

        */
    }

    /**
     * Performs a pull for commits with a repository with a force push.
     */
    @Test
    public void pullRepositoryForcePush() {
        /*
        // repository is already cloned
        unzip(FORCE_PUSH_REPOSITORY, ABSOLUTE_PATH_TO_REPOS + "/" + gitRepository.getId());

        // commits until 9c8c86 already tracked and commit 8926f7 is not on origin anymore
        initializeCommit(HASH_39E1A8, Collections.singletonList(masterBranch));
        initializeCommit(HASH_9C8C86, Collections.singletonList(masterBranch));
        GitCommit masterHead = initializeCommit(HASH_8926F7, Collections.singletonList(masterBranch));

        masterBranch.setLocalHead(masterHead);
        gitTrackingAccess.updateRepository(gitRepository);

        when(cleanUpCommits.cleanUp(any(), eq(gitRepository))).thenReturn(Collections.singletonList(HASH_8926F7));

        Collection<GitCommit> untrackedCommits = gitHandler.pullFromRepository(gitRepository);

        assertNotNull(untrackedCommits);

        assertEquals(8, gitRepository.getAllCommitHashes().size());
        assertFalse(gitRepository.getAllCommitHashes().contains(HASH_8926F7));

        verify(cleanUpCommits).cleanUp(any(), eq(gitRepository));
        verify(resultDeleter).deleteBenchmarkingResults(HASH_8926F7);

         */
    }

    /**
     * Test for cloning the lean repository.
     * Disabled because it takes very long.
     */
    @Test @Disabled
    public void leanTest() {
        initializeGitHandler(RSA);

        when(gitRepository.getId()).thenReturn(200);
        when(gitRepository.getPullURL()).thenReturn("git@github.com:leanprover/lean.git");
        when(gitTrackingAccess.containsCommit("ceacfa7445953cbc8860ddabc55407430a9ca5c3")).thenReturn(true);

        Collection<String> untrackedCommits = gitHandler.pullFromRepository(gitRepository);

        // account for the known commit
        final int expectedCommits = 13724 - 1;
        assertEquals(expectedCommits, untrackedCommits.size());
    }

    private void assertCommit(GitCommit commit, int parentCount, Collection<GitBranch> branches) {
        assertNotNull(commit);
        assertNotNull(commit.getCommitHash());
        assertNotNull(commit.getCommitDate());
        assertNotNull(commit.getAuthorDate());
        assertNotNull(commit.getEntryDate());
        assertNotNull(commit.getMessage());
        assertNotNull(commit.getLabels());

        assertEquals(gitRepository.getId(), commit.getRepositoryID());
        assertEquals(parentCount, commit.getParentHashes().size());
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

}
