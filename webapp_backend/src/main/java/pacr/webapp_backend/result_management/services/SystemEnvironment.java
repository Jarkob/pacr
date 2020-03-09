package pacr.webapp_backend.result_management.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Getter
@NoArgsConstructor
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
     * Creates a system environment. Copies all data from the given system environment.
     * @param sysEnv the system environment. Cannot be null.
     */
    SystemEnvironment(@NotNull final ISystemEnvironment sysEnv) {
        Objects.requireNonNull(sysEnv);

        this.name = sysEnv.getComputerName();
        this.os = sysEnv.getOs();
        this.processor = sysEnv.getProcessor();
        this.kernel = sysEnv.getKernel();
        this.cores = sysEnv.getCores();
        this.memory = sysEnv.getRam();
    }

    @Override
    public String getComputerName() {
        return name;
    }

    @Override
    public long getRam() {
        return this.memory;
    }
}
