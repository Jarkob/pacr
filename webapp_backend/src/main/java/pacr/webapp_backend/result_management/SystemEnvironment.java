package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.ISystemEnvironment;

/**
 * Represents the specification of a computer system that was used to benchmark a certain commit.
 */
class SystemEnvironment implements ISystemEnvironment {
    private String os;
    private String kernel;
    private int cores;
    private long memory;

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
