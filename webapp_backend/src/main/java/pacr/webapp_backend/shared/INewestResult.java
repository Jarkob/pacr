package pacr.webapp_backend.shared;

/**
 * Gets the newest result of a repository.
 */
public interface INewestResult extends ISubject {

    /**
     * Gets the newest saved benchmarking result for a repository. Returns null if the repository has no results saved
     * yet.
     * @param repositoryID the id of the repository.
     * @return the newest result.
     */
    IBenchmarkingResult getNewestResult(int repositoryID);
}
