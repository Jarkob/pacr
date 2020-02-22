package pacr.webapp_backend.benchmarker_communication.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pacr.webapp_backend.benchmarker_communication.services.SystemEnvironment;
import pacr.webapp_backend.shared.IJob;

/**
 * Represents a PACR-Benchmarker and its current status.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Benchmarker {

    private String address;

    private SystemEnvironment systemEnvironment;

    private IJob currentJob;

}
