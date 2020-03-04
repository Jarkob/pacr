package pacr.webapp_backend.result_management.services;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * A single result with an optional error message.
 */
@Getter
public class ResultWithError {

    private Double result;
    private String errorMessage;

    /**
     * Creates a ResultWithError. The result is only copied from the property result if there is no error.
     * @param propertyResult the result. Cannot be null.
     */
    ResultWithError(@NotNull final BenchmarkPropertyResult propertyResult) {
        Objects.requireNonNull(propertyResult);

        if (propertyResult.isError()) {
            this.result = null;
            this.errorMessage = propertyResult.getError();
        } else {
            this.result = propertyResult.getMedian();
            this.errorMessage = null;
        }
    }
}
