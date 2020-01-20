package pacr.webapp_backend.result_management;

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
     * @param sysEnv the system environment. Throws IllegalArgumentException if it is null.
     */
    SystemEnvironment(@NotNull ISystemEnvironment sysEnv) {
        if (sysEnv == null) {
            throw new IllegalArgumentException("system environment cannot be null");
        }
        this.name = sysEnv.getComputerName();
        this.os = sysEnv.getOS();
        this.processor = sysEnv.getProcessor();
        this.kernel = sysEnv.getKernel();
        this.cores = sysEnv.getCores();
        this.memory = sysEnv.getRamMemory();
    }

    /**
     * Creates a system environment. Throws IllegalArgumentException if strings are empty or blank. Input may be null
     * (this implies a detection error for the given input).
     * @param name the computers name. Cannot be empty or blank.
     * @param os the os. Cannot be empty or blank.
     * @param processor the processor model. Cannot be empty or blank.
     * @param kernel the kernel. Cannot be empty or blank.
     * @param cores the number of cores.
     * @param memory the amount of memory in GB.
     */
    public SystemEnvironment(String name, String os, String processor, String kernel, int cores, long memory) {
        if (isInputStringInvalid(name) || isInputStringInvalid(os) || isInputStringInvalid(processor)
                || isInputStringInvalid(kernel)) {
            throw new IllegalArgumentException("input cannot be empty or blank");
        }
        this.name = name;
        this.os = os;
        this.processor = processor;
        this.kernel = kernel;
        this.cores = cores;
        this.memory = memory;
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

    private boolean isInputStringInvalid(String string) {
        if (string == null) {
            return false;
        }
        return string.isEmpty() || string.isBlank();
    }
}
