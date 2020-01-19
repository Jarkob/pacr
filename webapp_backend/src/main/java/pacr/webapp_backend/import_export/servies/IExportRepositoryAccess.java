package pacr.webapp_backend.import_export.servies;

import pacr.webapp_backend.shared.IRepository;

/**
 * Represents the interface for accessing metadata of repositories.
 */
public interface IExportRepositoryAccess {

    /**
     * Gets metadata of a repository with the given id.
     *
     * @param id the id of the repository.
     * @return a repository.
     */
    IRepository findGitRepositoryById(int id);

}
