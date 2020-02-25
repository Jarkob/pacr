package pacr.webapp_backend.result_management.services;

import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * A single result with an optional error message.
 */
public class ResultWithError {

    private Double result;
    private String errorMessage;

    /**
     * Creates a ResultWithError. The result is only copied from the property result if there is no error.
     * @param propertyResult the result. Cannot be null.
     */
    ResultWithError(@NotNull BenchmarkPropertyResult propertyResult) {
        Objects.requireNonNull(propertyResult);

        if (propertyResult.isError()) {
            this.result = null;
            this.errorMessage = propertyResult.getError();
        } else {
            this.result = propertyResult.getMedian();
            this.errorMessage = null;
        }
    }

    /**
     * @return gets the result. Null if there was an error.
     */
    public Double getResult() {
        return result;
    }

    /**
     * @return gets the error message. Null if there was no error.
     */
    public String getErrorMessage() {
        return errorMessage;
    }
}
