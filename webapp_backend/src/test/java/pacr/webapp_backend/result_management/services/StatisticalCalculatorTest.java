package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticalCalculatorTest {

    @Test
    void getQuanitle_parameterIsNull_shouldReturnErrorCode() {
        assertEquals(StatisticalCalculator.ERROR_CODE, StatisticalCalculator.getQuantile(0, null));
    }

    @Test
    void getMean_parameterIsNull_shouldReturnErrorCode() {
        assertEquals(StatisticalCalculator.ERROR_CODE, StatisticalCalculator.getMean(null));
    }

    @Test
    void getStandardDeviation_parameterIsNull_shouldReturnErrorCode() {
        assertEquals(StatisticalCalculator.ERROR_CODE, StatisticalCalculator.getStandardDeviation(null));
    }
}
