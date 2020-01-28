package pacr.benchmarker.services.git;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class GitHandler {

    private static final Logger LOGGER = LogManager.getLogger(GitHandler.class);

    private String absWorkingDir;
    private String relWorkingDir;

    private TransportConfigCallback transportConfigCallback;

    public GitHandler() {
        this.transportConfigCallback = new SSHTransportConfigCallback();
        relWorkingDir = "repositories";
        absWorkingDir = System.getProperty("user.dir") + "/" + relWorkingDir;
    }

    public String setupRepositoryForBenchmark(String repositoryURL, String commitHash) {

        // can't use repository as directory name because of special characters
        int directoryName = repositoryURL.hashCode();

        String absRepositoryDir = absWorkingDir + "/" + directoryName;
        String relRepositoryDir = relWorkingDir + "/" + directoryName;

        LOGGER.info("Setting up repository {} for benchmarking with commit {}.", repositoryURL, commitHash);

        Git git = initializeGit(absRepositoryDir, repositoryURL);
        if (git == null) {
            return null;
        }

        try {
            git.checkout()
                    .setName(commitHash)
                    .call();
        } catch (GitAPIException | JGitInternalException e) {
            LOGGER.error("Could not checkout to commit hash {}.", commitHash);
            return null;
        }

        git.getRepository().close();
        git.close();

        return relRepositoryDir;
    }

    private Git initializeGit(String directoryPath, String repositoryURL) {
        File directory = new File(directoryPath);

        if (!directory.exists()) {
            try {
                Git git = Git.cloneRepository()
                        .setDirectory(directory)
                        .setTransportConfigCallback(transportConfigCallback)
                        .setURI(repositoryURL)
                        .call();

                git.getRepository().close();
                git.close();
                return git;
            } catch (GitAPIException e) {
                LOGGER.error("Could not clone repository with URL {}.", repositoryURL);
                return null;
            }
        } else {
            Repository repository = null;
            try {
                repository = getRepository(directory.getAbsolutePath());
            } catch (IOException e) {
                return null;
            }

            Git git = new Git(repository);

            try {
                git.fetch().setTransportConfigCallback(transportConfigCallback).call();
            } catch (GitAPIException e) {
                LOGGER.error("Could not fetch repository with URL {}.", repositoryURL);
            }

            return git;
        }
    }

    private Repository getRepository(String path) throws IOException {
        assert path != null;

        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        return repositoryBuilder.setGitDir(
                new File(path + "/.git"))
                .readEnvironment()
                .findGitDir()
                .setMustExist(true)
                .build();
    }

}
