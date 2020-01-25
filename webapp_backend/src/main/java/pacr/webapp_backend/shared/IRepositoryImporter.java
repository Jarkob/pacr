package pacr.webapp_backend.shared;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Can import a repository.
 *
 * @author Pavel Zwerschke
 */
public interface IRepositoryImporter {

    /**
     * Adds a repository.
     * @param repositoryURL is the pull URL of the repository.
     * @param observeFromDate is the date from when it should be observed.
     * @param name is the name of the repository.
     * @param branchNames are the names of the selected branches.
     * @return the ID of the repository.
     */
    int addRepository(String repositoryURL, LocalDate observeFromDate, String name, Collection<String> branchNames);

}
