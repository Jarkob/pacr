package pacr.webapp_backend.shared;

import javassist.NotFoundException;

/**
 * Gets the newest result of a repository.
 */
public interface INewestResult extends ISubject {

    /**
     * Gets the newest saved benchmarking result for a repository. Returns null if the repository has no results saved
     * yet.
     * @param repositoryID the id of the repository.
     * @return the newest result.
     * @throws NotFoundException if no repository with this id could be found.
     */
    IBenchmarkingResult getNewestResult(int repositoryID) throws NotFoundException;
}
