package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.ISystemEnvironment;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
     * @param sysEnv the system environment.
     */
    SystemEnvironment(ISystemEnvironment sysEnv) {
        this.os = sysEnv.getOS();
        this.kernel = sysEnv.getKernel();
        this.cores = sysEnv.getCores();
        this.memory = sysEnv.getRamMemory();
    }

    /**
     * Creates a system environment.
     * @param os the os
     * @param kernel the processor
     * @param cores the number of cores
     * @param memory the amount of memory in GB
     */
    public SystemEnvironment(String os, String kernel, int cores, long memory) {
        this.os = os;
        this.kernel = kernel;
        this.cores = cores;
        this.memory = memory;
    }

    @Override
    public String getOS() {
        return this.os;
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
