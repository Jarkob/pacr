package pacr.webapp_backend.result_management.services;

import pacr.webapp_backend.shared.ISystemEnvironment;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Represents the specification of a computer system that was used to benchmark a certain commit.
 */
@Entity
public class SystemEnvironment implements ISystemEnvironment {
    @Id
    @GeneratedValue
    private int id;

    private String name;
    private String os;
    private String processor;
    private String kernel;
    private int cores;
    private long memory;

    /**
     * Creates empty system environment. Needed for jpa.
     */
    public SystemEnvironment() {
    }

    /**
     * Creates a system environment. Copies all data from the given system environment.
     * @param sysEnv the system environment. Cannot be null.
     */
    SystemEnvironment(@NotNull ISystemEnvironment sysEnv) {
        Objects.requireNonNull(sysEnv);

        this.name = sysEnv.getComputerName();
        this.os = sysEnv.getOS();
        this.processor = sysEnv.getProcessor();
        this.kernel = sysEnv.getKernel();
        this.cores = sysEnv.getCores();
        this.memory = sysEnv.getRamMemory();
    }

    @Override
    public String getComputerName() {
        return name;
    }

    @Override
    public String getOS() {
        return this.os;
    }

    @Override
    public String getProcessor() {
        return processor;
    }

    @Override
    public String getKernel() {
        return this.kernel;
    }

    @Override
    public int getCores() {
        return this.cores;
    }

    @Override
    public long getRamMemory() {
        return this.memory;
    }
}
