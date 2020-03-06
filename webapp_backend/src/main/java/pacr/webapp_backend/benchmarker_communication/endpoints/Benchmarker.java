package pacr.webapp_backend.benchmarker_communication.endpoints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.benchmarker_communication.services.SystemEnvironment;
import pacr.webapp_backend.shared.IJob;

/**
 * Represents a PACR-Benchmarker and its current status.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Benchmarker implements Comparable<Benchmarker> {

    private String address;

    private SystemEnvironment systemEnvironment;

    private IJob currentJob;

    /**
     * Compare two Benchmarker with their address.
     * @param o the benchmarker which is compared.
     * @return the result of the comparison of the addresses.
     */
    @Override
    public int compareTo(@NotNull final Benchmarker o) {
        if (StringUtils.hasText(address)) {
            return address.compareTo(o.address);
        }

        return 1;
    }
}
