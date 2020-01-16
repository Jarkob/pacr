package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.IResultDeleter;

public class ResultDeleterDummy implements IResultDeleter {

    @Override
    public void deleteBenchmarkingResults(String commitHash) {

    }

    @Override
    public void deleteAllResultsForRepository(int repositoryID) {

    }
}
