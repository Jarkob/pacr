package pacr.webapp_backend.shared;

public interface IResultDeleter {

    void deleteBenchmarkingResults(String commitHash);

    void deleteAllResultsForRepository(int repositoryID);

}
