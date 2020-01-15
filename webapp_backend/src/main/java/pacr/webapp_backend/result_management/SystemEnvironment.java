package pacr.webapp_backend.result_management;

import jdk.jshell.spi.ExecutionControl;
import pacr.webapp_backend.shared.ISystemEnvironment;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Represents the specification of a computer system that was used to benchmark a certain commit.
 */
@Entity
public class SystemEnvironment implements ISystemEnvironment {
    @Id
    @GeneratedValue
    private int id;

    private String os;
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
     * @param sysEnv the system environment. Throws IllegalArgumentException if it is null.
     */
    SystemEnvironment(@NotNull ISystemEnvironment sysEnv) {
        if (sysEnv == null) {
            throw new IllegalArgumentException("system environment cannot be null");
        }
        this.os = sysEnv.getOS();
        this.kernel = sysEnv.getKernel();
        this.cores = sysEnv.getCores();
        this.memory = sysEnv.getRamMemory();
    }

    /**
     * Creates a system environment. Throws IllegalArgumentException if strings are null, empty or blank.
     * @param os the os. Cannot be empty or blank.
     * @param kernel the kernel. Cannot be empty or blank.
     * @param cores the number of cores.
     * @param memory the amount of memory in GB.
     */
    public SystemEnvironment(@NotNull String os, @NotNull String kernel, int cores, long memory) {
        if (!isInputStringValid(os) || !isInputStringValid(kernel)) {
            throw new IllegalArgumentException("input cannot be null, empty or blank");
        }
        this.os = os;
        this.kernel = kernel;
        this.cores = cores;
        this.memory = memory;
    }

    /**
     * Added so the code compiles.
     * TODO: @Martin
     */
    @Override
    public String getComputerName() {
        throw new UnsupportedOperationException("This has to be implemented in results management");
    }

    @Override
    public String getOS() {
        return this.os;
    }

    /**
     * Added so the code compiles.
     * TODO: @Martin
     */
    @Override
    public String getProcessor() {
        throw new UnsupportedOperationException("This has to be implemented in results management");
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

    private boolean isInputStringValid(String string) {
        if (string == null || string.isEmpty() || string.isBlank()) {
            return false;
        }
        return true;
    }
}
