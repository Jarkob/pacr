package pacr.webapp_backend.result_management;

import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.IResultSaver;

/**
 * IResultSaver with empty implementation. Needed for dependency injection
 */
@Component
public class ResultSaverDummy implements IResultSaver {
    @Override
    public void saveBenchmarkingResults(IBenchmarkingResult benchmarkingResult) {

    }
}
