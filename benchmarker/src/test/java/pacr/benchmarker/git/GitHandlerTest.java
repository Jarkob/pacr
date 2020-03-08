package pacr.benchmarker.git;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.benchmarker.services.git.GitHandler;
import pacr.benchmarker.services.git.SSHTransportConfigCallback;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for GitHandler.
 *
 * @author Pavel Zwerschke
 */
public class GitHandlerTest {

    private static final String REPOSITORY_URL = "git@git.scc.kit.edu:pacr/pacr-test-repository.git";
    private static final String REPOSITORY_URL2 = "git@git.scc.kit.edu:pacr/pacr-test-repository-2.git";
    private static final String COMMIT_HASH = "ac782173736902511a6f3214e0cac3068b27a448";
    private static final String COMMIT_HASH2 = "634c02e85bec85a1f07990724505f1c9d15737ed";
    private static final String COMMIT_HASH3 = "58711941d4bd3034b74868c202c89d9b3e2e138e";
    private static final String SSH_KEY = "/src/test/resources/pacr/benchmarker/services/git/ssh.key";
    private static final String WORKING_DIR = "/target/test";

    private GitHandler gitHandler;

    @BeforeEach
    public void setUp() {
        deleteDirectories();
        SSHTransportConfigCallback callback = new SSHTransportConfigCallback(SSH_KEY);
        gitHandler = new GitHandler(WORKING_DIR, callback);
    }

    @Test
    public void setupRepositoryForBenchmark() {
        String repoDir = gitHandler.setupRepositoryForBenchmark(REPOSITORY_URL, COMMIT_HASH);

        assertEquals(WORKING_DIR + "/" + REPOSITORY_URL.hashCode(), repoDir);
        File repo = new File(System.getProperty("user.dir") + WORKING_DIR + "/" + REPOSITORY_URL.hashCode());
        assertTrue(repo.exists());
    }

    @Test
    public void alreadyExists() throws IOException, GitAPIException {
        gitHandler.setupRepositoryForBenchmark(REPOSITORY_URL2, COMMIT_HASH3);
        gitHandler.setupRepositoryForBenchmark(REPOSITORY_URL2, COMMIT_HASH2);

        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        Repository repo = repositoryBuilder.setGitDir(
                new File(System.getProperty("user.dir")
                        + WORKING_DIR + "/" + REPOSITORY_URL2.hashCode() + "/.git"))
                .readEnvironment()
                .findGitDir()
                .setMustExist(true)
                .build();

        Git git = new Git(repo);
        assertEquals(COMMIT_HASH2, git.log().call().iterator().next().getName());
    }

    @Test
    public void invalidRepositoryName() {
        assertNull(gitHandler.setupRepositoryForBenchmark("inv", "hash"));
    }

    @AfterEach
    public void cleanUp() {
        deleteDirectories();
    }

    private void deleteDirectories() {
        try {
            FileUtils.deleteDirectory(new File(WORKING_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
