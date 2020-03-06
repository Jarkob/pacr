package pacr.webapp_backend.result_management.services;

import lombok.Getter;
import pacr.webapp_backend.shared.ResultInterpretation;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Represents a slimmed down version of a BenchmarkPropertyResult for output. This only contains the statistical values
 * of the original BenchmarkPropertyResult, not all measured data.
 * '@Getter' provides this class with all getters so the json can be properly created.
 */
@Getter
public class OutputPropertyResult {
    private final String name;
    private final String unit;
    private final ResultInterpretation interpretation;

    private final double mean;
    private final double lowerQuartile;
    private final double median;
    private final double upperQuartile;
    private final double standardDeviation;
    private final boolean hadLocalError;
    private final String errorMessage;

    private final double ratioToPreviousCommit;
    private final boolean compared;

    /**
     * Creates a OutputPropertyResult from a BenchmarkPropertyResult. Copies all statistical data and the associated
     * property.
     * @param result the BenchmarkPropertyResult that this OutputPropertyResult is cloned from. Cannot be null.
     */
    OutputPropertyResult(@NotNull final BenchmarkPropertyResult result) {
        Objects.requireNonNull(result);

        this.name = result.getName();
        this.unit = result.getUnit();
        this.interpretation = result.getResultInterpretation();

        this.mean = result.getMean();
        this.lowerQuartile = result.getLowerQuartile();
        this.median = result.getMedian();
        this.upperQuartile = result.getUpperQuartile();
        this.standardDeviation = result.getStandardDeviation();
        this.hadLocalError = result.isError();
        this.errorMessage = result.getError();

        this.ratioToPreviousCommit = result.getRatio();
        this.compared = result.isCompared();
    }
}
