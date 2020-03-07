package pacr.webapp_backend.benchmarker_communication.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import pacr.webapp_backend.shared.ISystemEnvironment;

/**
 * Represents information about the system environment of a PACR-Benchmarker.
 */
@Getter
@NoArgsConstructor
public class SystemEnvironment implements ISystemEnvironment {

    private String computerName;
    private String os;
    private String kernel;
    private String processor;
    private int cores;
    private long ram; // in GiB

}
