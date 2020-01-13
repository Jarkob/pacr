package pacr.webapp_backend.benchmarker_communication.endpoints;

import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.benchmarker_communication.services.BenchmarkerJob;
import pacr.webapp_backend.benchmarker_communication.services.IJobSender;

/**
 * Stub so the code compiles.
 */
@RestController
public class BenchmarkingJobsController implements IJobSender {
    @Override
    public boolean sendJob(BenchmarkerJob benchmarkerJob) {
        return true;
    }
}
