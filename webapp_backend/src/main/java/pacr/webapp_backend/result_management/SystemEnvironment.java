package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.ISystemEnvironment;

/**
 * Represents the specification of a computer system that was used to benchmark a certain commit.
 */
class SystemEnvironment implements ISystemEnvironment {
    private String commitHash;
    private String os;
    private String kernel;
    private int cores;
    private long memory;

    /**
     * Creates a system environment for a commit. Copies all data from the given system environment.
     * @param commitHash the hash of the commit.
     * @param sysEnv the system environment.
     */
    SystemEnvironment(String commitHash, ISystemEnvironment sysEnv) {
        this.commitHash = commitHash;
        this.os = sysEnv.getOS();
        this.kernel = sysEnv.getKernel();
        this.cores = sysEnv.getCores();
        this.memory = sysEnv.getMemory();
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
    public long getMemory() {
        return this.memory;
    }

    /**
     * Gets the commit hash of the commit that was benchmarked on this system environment.
     * @return the commit hash.
     */
    String getCommitHash() {
        return this.commitHash;
    }
}
