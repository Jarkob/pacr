package pacr.webapp_backend.benchmarker_communication.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pacr.webapp_backend.benchmarker_communication.services.BenchmarkerJob;
import pacr.webapp_backend.benchmarker_communication.services.SystemEnvironment;

/**
 * Represents a PACR-Benchmarker and its current status.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Benchmarker {

    private SystemEnvironment systemEnvironment;

    private BenchmarkerJob currentJob;

}
