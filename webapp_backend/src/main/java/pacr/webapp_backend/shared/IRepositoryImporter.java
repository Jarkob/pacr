package pacr.webapp_backend.shared;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

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
    int addRepository(@NotNull String repositoryURL, LocalDate observeFromDate,
                      @NotNull String name, @NotNull Set<String> branchNames);
}
