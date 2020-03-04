package pacr.webapp_backend.git_tracking.services.git;

import net.lingala.zip4j.exception.ZipException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.git_tracking.services.entities.GitBranch;
import pacr.webapp_backend.git_tracking.services.entities.GitCommit;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Collections;

import net.lingala.zip4j.core.ZipFile;
import pacr.webapp_backend.shared.IResultDeleter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;

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

    // branches
    private static final String MASTER = "master";
    private static final String TEST_BRANCH1 = "testbranch1";
    private static final String TEST_BRANCH2 = "testbranch2";

    private GitHandler gitHandler;
    private GitRepository gitRepository;
    private IGitTrackingAccess gitTrackingAccess;
    @Mock
    private ICleanUpCommits cleanUpCommits;
    @Mock
    private IResultDeleter resultDeleter;

    @Autowired
    public GitHandlerTest(final IGitTrackingAccess gitTrackingAccess) {
        this.gitTrackingAccess = gitTrackingAccess;
    }

    @BeforeAll
    public static void deleteFolders() {
        final File repos = new File(ABSOLUTE_PATH_TO_REPOS);
        if (repos.exists()) {
            try {
                FileUtils.deleteDirectory(repos);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AfterEach
    public void cleanDatabase() {
        gitTrackingAccess.removeRepository(gitRepository.getId());

        gitTrackingAccess.removeCommits(gitTrackingAccess.getAllCommitHashes(gitRepository.getId()));
    }

    @AfterAll
    public static void cleanUp() {
        deleteFolders();
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // create repo
        gitRepository = new GitRepository(false,
                PULL_URL, "testing repo", "#ffffff", null);
        gitRepository.setSelectedBranches(new HashSet<>(Arrays.asList(MASTER, TEST_BRANCH1)));
        gitTrackingAccess.addRepository(gitRepository);

        initializeGitHandler();

        // update branches in repo
        gitHandler.setBranchesToRepo(gitRepository);
        gitTrackingAccess.updateRepository(gitRepository);
        gitRepository = gitTrackingAccess.getRepository(gitRepository.getId());

    }

    private void initializeGitHandler() {
        final TransportConfigCallback transportConfigCallback
                = new SSHTransportConfigCallback(RSA);

        // create GitHandler
        try {
            gitHandler = new GitHandler(
                    PATH_TO_REPOS, transportConfigCallback, gitTrackingAccess, cleanUpCommits, resultDeleter,
                    "#pacr-ignore", "#pacr-label");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    private GitCommit initializeCommit(final String commitHash, final Collection<GitBranch> branches) {
        final GitCommit commit = new GitCommit(commitHash, "msg", LocalDateTime.now(), LocalDateTime.now(),
                gitRepository);
        for (final GitBranch branch : branches) {
            commit.addBranch(branch);
        }
        gitTrackingAccess.addCommit(commit);
        return commit;
    }

    /**
     * Tries to clone a private repository,
     * but the SSH Public Key is not saved in the repository.
     */
    @Test
    public void cloneRepositoryNoAuthorization() {
        final GitRepository mockRepository = mock(GitRepository.class);

        when(mockRepository.getPullURL()).thenReturn(PULL_URL_NO_AUTHORIZATION);

        assertThrows(GitAPIException.class, () -> gitHandler.cloneRepository(mockRepository));
    }

    /**
     * Performs a pull for commits with an uninitialized repository.
     */
    @Test
    public void pullNewRepository() throws PullFromRepositoryException {

        final Collection<String> untrackedCommitHashes = gitHandler.pullFromRepository(gitRepository);

        assertNotNull(untrackedCommitHashes);
        assertEquals(8, untrackedCommitHashes.size());

        final GitBranch masterBranch = gitRepository.getTrackedBranch(MASTER);
        final GitBranch testBranch1 = gitRepository.getTrackedBranch(TEST_BRANCH1);

        assertEquals(HASH_AC7821, masterBranch.getHeadHash());
        assertEquals(HASH_6FDE0D, testBranch1.getHeadHash());

        final Collection<GitCommit> untrackedCommits = gitTrackingAccess.getAllCommits(gitRepository.getId());

        assertCommit(getCommitWithHash(HASH_E68151, untrackedCommits),
                1, Arrays.asList(masterBranch, testBranch1));
        assertEquals("Add new file",
                Objects.requireNonNull(getCommitWithHash(HASH_E68151, untrackedCommits)).getCommitMessage());
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

    }

    /**
     * Performs a pull for commits with a repository that is already initialized.
     */
    @Test
    public void pullRepositoryWithNewCommits() throws PullFromRepositoryException {
        // repository is already cloned
        unzip(NEW_COMMITS_REPOSITORY, ABSOLUTE_PATH_TO_REPOS + "/" + gitRepository.getId());

        final GitBranch masterBranch = gitRepository.getTrackedBranch(MASTER);
        final GitBranch testBranch1 = gitRepository.getTrackedBranch(TEST_BRANCH1);

        // commits until e68151 already tracked for testbranch1, until 9c8c86 for master
        initializeCommit(HASH_39E1A8, Arrays.asList(masterBranch, testBranch1));
        final GitCommit headMaster = initializeCommit(HASH_9C8C86, Arrays.asList(masterBranch, testBranch1));
        final GitCommit headTestBranch1 = initializeCommit(HASH_E68151, Collections.singletonList(testBranch1));

        masterBranch.setHeadHash(headMaster.getCommitHash());
        testBranch1.setHeadHash(headTestBranch1.getCommitHash());
        gitTrackingAccess.updateRepository(gitRepository);

        final Set<String> untrackedCommitHashes = gitHandler.pullFromRepository(gitRepository);

        final GitCommit updatedTracked3 = gitTrackingAccess.getCommit(HASH_E68151);

        assertEquals(2, updatedTracked3.getBranches().size());

        assertNotNull(untrackedCommitHashes);
        assertEquals(5, untrackedCommitHashes.size());

        final Collection<GitCommit> untrackedCommits = new HashSet<>();
        for (final String commitHash : untrackedCommitHashes) {
            untrackedCommits.add(gitTrackingAccess.getCommit(commitHash));
        }

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
    }

    /**
     * Performs a pull for commits with a repository with a force push.
     */
    @Test
    public void pullRepositoryForcePush() throws PullFromRepositoryException {

        // repository is already cloned
        unzip(FORCE_PUSH_REPOSITORY, ABSOLUTE_PATH_TO_REPOS + "/" + gitRepository.getId());

        final GitBranch masterBranch = gitRepository.getTrackedBranch(MASTER);

        // commits until 9c8c86 already tracked and commit 8926f7 is not on origin anymore
        initializeCommit(HASH_39E1A8, Collections.singletonList(masterBranch));
        initializeCommit(HASH_9C8C86, Collections.singletonList(masterBranch));
        final GitCommit masterHead = initializeCommit(HASH_8926F7, Collections.singletonList(masterBranch));

        masterBranch.setHeadHash(masterHead.getCommitHash());
        gitTrackingAccess.updateRepository(gitRepository);

        when(cleanUpCommits.cleanUp(any(), eq(gitRepository), eq(gitTrackingAccess)))
                .thenReturn(new HashSet<>(Collections.singletonList(HASH_8926F7)));

        final Collection<String> untrackedCommits = gitHandler.pullFromRepository(gitRepository);

        assertNotNull(untrackedCommits);

        final Collection<String> commitHashes = gitTrackingAccess.getAllCommitHashes(gitRepository.getId());
        assertEquals(8, commitHashes.size());
        assertFalse(commitHashes.contains(HASH_8926F7));

        verify(cleanUpCommits).cleanUp(any(), eq(gitRepository), eq(gitTrackingAccess));
        verify(resultDeleter).deleteBenchmarkingResults(new HashSet<>(Arrays.asList(HASH_8926F7)));
    }

    /**
     * Test for cloning the lean repository.
     */
    @Test @Disabled // disabled because it takes very long
    public void leanTest() throws PullFromRepositoryException {
        gitRepository.setName("LEAN");
        gitRepository.setIsHookSet(true); // PullIntervalScheduler should not interfere
        gitRepository.setPullURL("git@github.com:leanprover/lean.git");
        gitRepository.setSelectedBranches(new HashSet<>(Arrays.asList(MASTER, "NewParserAttr", "test")));

        gitTrackingAccess.updateRepository(gitRepository);

        // update branches in repo
        gitHandler.setBranchesToRepo(gitRepository);
        gitTrackingAccess.updateRepository(gitRepository);
        gitRepository = gitTrackingAccess.getRepository(gitRepository.getId());

        final GitBranch masterBranch = gitRepository.getTrackedBranch(MASTER);

        final Collection<String> untrackedCommits = gitHandler.pullFromRepository(gitRepository);

        // account for the known commit
        final int expectedCommits = 13724;
        assertEquals(expectedCommits, untrackedCommits.size());
    }

    private void assertCommit(final GitCommit commit, final int parentCount, final Collection<GitBranch> branches) {
        assertNotNull(commit);
        assertNotNull(commit.getCommitHash());
        assertNotNull(commit.getCommitDate());
        assertNotNull(commit.getAuthorDate());
        assertNotNull(commit.getEntryDate());
        assertNotNull(commit.getCommitMessage());
        assertNotNull(commit.getLabels());

        assertEquals(gitRepository.getId(), commit.getRepositoryID());
        assertEquals(parentCount, commit.getParentHashes().size());
        assertGitBranchCollectionEquals(branches, commit.getBranches());
    }

    private void assertGitBranchCollectionEquals(final Collection<GitBranch> expected, final Collection<GitBranch> actual) {
        assertEquals(expected.size(), actual.size());
        for (final GitBranch expectedBranch : expected) {
            final GitBranch actualBranch = getBranchFromCollection(actual, expectedBranch.getName());
            assertNotNull(actualBranch);
        }
    }

    private GitBranch getBranchFromCollection(final Collection<GitBranch> branches, final String name) {
        for (final GitBranch branch : branches) {
            if (branch.getName().equals(name)) {
                return branch;
            }
        }
        return null;
    }

    private void unzip(final String zip, final String destination) {
        try {
            final ZipFile zipFile = new ZipFile(zip);
            zipFile.extractAll(destination);
        } catch (final ZipException e) {
            e.printStackTrace();
            fail();
        }
    }

    private GitCommit getCommitWithHash(final String startsWith, final Collection<GitCommit> commits) {
        for (final GitCommit commit : commits) {
            if (commit.getCommitHash().startsWith(startsWith)) {
                return commit;
            }
        }
        return null;
    }

}
