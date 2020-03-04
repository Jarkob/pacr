package pacr.webapp_backend.git_tracking.services.git;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pacr.webapp_backend.git_tracking.services.entities.GitRepository;
import pacr.webapp_backend.git_tracking.services.IGitTrackingAccess;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Test case for cleanUpCommits.
 *
 * @author Pavel Zwerschke
 */
public class CleanUpCommitsTest {

    private static final String PATH_TO_REPOS = "/target/test/repos";
    private static final String ABSOLUTE_PATH_TO_REPOS = System.getProperty("user.dir") + PATH_TO_REPOS;
    private static final String RESOURCES = "/src/test/resources/pacr/webapp_backend/git_tracking/services/git";
    private static final String RSA = RESOURCES + "/id_rsa";
    private static final String FORCE_PUSH_REPOSITORY = System.getProperty("user.dir") + RESOURCES + "/forcePush.zip";
    private static final int REPOSITORY_ID = 39;

    private static final String HASH_39E1A8 = "39e1a8c8f9951015a101c18c55533947d0a44bdd";
    private static final String HASH_9C8C86 = "9c8c86f5939c88329d9f46f7f5266f6c6b2e515e";
    private static final String HASH_8926F7 = "8926f7e91d42eb8b5b88343694bd6cd311d6e180";

    private CleanUpCommits cleanUpCommits;
    private Git git;
    @Mock
    private GitRepository gitRepository;
    @Mock
    private IGitTrackingAccess gitTrackingAccess;

    @BeforeEach
    public void setUp() {
        deleteFolders();
        MockitoAnnotations.initMocks(this);
        cleanUpCommits = new CleanUpCommits();

        when(gitRepository.getId()).thenReturn(REPOSITORY_ID);
    }

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

    @Test
    public void cleanUpCommits() {
        unzip(FORCE_PUSH_REPOSITORY, ABSOLUTE_PATH_TO_REPOS + "/" + REPOSITORY_ID);
        initializeGit();

        final String MASTER = "master";
        final String TEST_BRANCH1 = "testbranch1";
        final String TEST_BRANCH2 = "testbranch2";

        // tracked branches
        when(gitRepository.isBranchSelected(MASTER)).thenReturn(true);
        when(gitRepository.isBranchSelected(TEST_BRANCH1)).thenReturn(true);
        when(gitRepository.isBranchSelected(TEST_BRANCH2)).thenReturn(false);

        // commits until 9c8c86 already tracked and commit 8926f7 is not on origin anymore
        when(gitTrackingAccess.containsCommit(anyString())).thenReturn(false);
        when(gitTrackingAccess.containsCommit(HASH_39E1A8)).thenReturn(true);

        when(gitTrackingAccess.containsCommit(HASH_9C8C86)).thenReturn(true);

        when(gitTrackingAccess.containsCommit(HASH_8926F7)).thenReturn(true);

        when(gitTrackingAccess.getAllCommitHashes(gitRepository.getId())).thenReturn(new HashSet<>(Arrays.asList(
                HASH_39E1A8,
                HASH_9C8C86,
                HASH_8926F7)));

        final Collection<String> toDelete = cleanUpCommits.cleanUp(git, gitRepository, gitTrackingAccess);

        assertNotNull(toDelete);
        assertEquals(1, toDelete.size());
        assertEquals(HASH_8926F7, toDelete.iterator().next());
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

    private void initializeGit() {
        // initialize Git
        try {
            final FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
            final Repository repository = repositoryBuilder.setGitDir(
                    new File(ABSOLUTE_PATH_TO_REPOS + "/" + REPOSITORY_ID + "/.git"))
                    .readEnvironment()
                    .findGitDir()
                    .setMustExist(true)
                    .build();
            git = new Git(repository);
            // fetch
            git.fetch().setRemote("origin")
                    .setTransportConfigCallback(new SSHTransportConfigCallback(RSA))
                    .call();
        } catch (final IOException | GitAPIException e) {
            e.printStackTrace();
            fail();
        }
    }

}
