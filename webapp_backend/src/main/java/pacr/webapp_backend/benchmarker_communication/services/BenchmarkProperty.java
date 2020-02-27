package pacr.webapp_backend.benchmarker_communication.services;

import java.util.Collection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;
import pacr.webapp_backend.shared.IBenchmarkProperty;
import pacr.webapp_backend.shared.ResultInterpretation;

/**
 * Represents a property of a benchmark.
 */
@NoArgsConstructor
@Setter
public class BenchmarkProperty implements IBenchmarkProperty {

    @Getter
    private Collection<Double> results;

    @Getter
    private ResultInterpretation resultInterpretation;

    @Getter
    private String unit;

    private String error;

    @Override
    public String getError() {
        if (!StringUtils.hasText(error)) {
            return null;
        }
        
        return error;
    }

    @Override
    public boolean isError() {
        return StringUtils.hasText(error);
    }

}
