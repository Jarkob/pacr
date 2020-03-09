package pacr.webapp_backend.database;

import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Gives access to both a CommitDB and a RepositoryDB.
 */
@Component
public class CommitRepositoryDB {

    /**
     * Protected so subclasses can use these jpa repositories.
     */
    final CommitDB commitDB;
    final RepositoryDB repositoryDB;

    /**
     * Creates a new instance of CommitRepositoryDB.
     * @param commitDB is the JPA commit access interface.
     * @param repositoryDB is the JPA repository access interface.
     */
    public CommitRepositoryDB(@NotNull final CommitDB commitDB, @NotNull final RepositoryDB repositoryDB) {
        Objects.requireNonNull(commitDB);
        Objects.requireNonNull(repositoryDB);

        this.commitDB = commitDB;
        this.repositoryDB = repositoryDB;
    }
}
