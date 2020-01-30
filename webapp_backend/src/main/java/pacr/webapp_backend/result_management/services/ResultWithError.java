package pacr.webapp_backend.result_management.services;

import org.springframework.lang.Nullable;

/**
 * A single result with an optional error message.
 */
public class ResultWithError {

    private Double result;
    private String errorMessage;

    /**
     * Creates a ResultWithError.
     * @param result the result. May be null if there was an error. Otherwise no error is assumed.
     * @param errorMessage the error message. May be null if there was no error. Otherwise an error is assumed.
     */
    ResultWithError(@Nullable Double result, @Nullable String errorMessage) {
        this.result = result;
        this.errorMessage = errorMessage;
    }

    /**
     * @return gets the result. Null if there was an error.
     */
    Double getResult() {
        return result;
    }

    /**
     * @return gets the error message. Null if there was no error.
     */
    String getErrorMessage() {
        return errorMessage;
    }
}
