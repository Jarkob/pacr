package pacr.webapp_backend.result_management;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.IResultSaver;

@Component
public class ResultSaverDummy implements IResultSaver {
    public void saveBenchmarkingResults(IBenchmarkingResult benchmarkingResult) {

    }
}
