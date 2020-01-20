package pacr.webapp_backend.result_management.services;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.database.BenchmarkDB;
import pacr.webapp_backend.database.ResultDB;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.shared.IEventHandler;
import pacr.webapp_backend.shared.IResultSaver;

@SpringBootTest
public class ResultBenchmarkSaverTest {

    private IResultAccess resultAccess;
    private ResultBenchmarkSaver resultBenchmarkSaver;

    @Mock
    private BenchmarkManager benchmarkManager;
    @Mock
    private ResultGetter resultGetter;
    @Mock
    private IEventHandler eventHandler;

    @Autowired
    public ResultBenchmarkSaverTest(IResultAccess resultAccess) {
        this.resultBenchmarkSaver = new ResultBenchmarkSaver(resultAccess, benchmarkManager, resultGetter, eventHandler,
                new GetCommitAccessStub());
    }
}
