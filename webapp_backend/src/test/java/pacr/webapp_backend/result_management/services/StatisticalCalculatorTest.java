package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.Test;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static pacr.webapp_backend.result_management.services.SimpleBenchmark.PROPERTY_NAME;
import static pacr.webapp_backend.result_management.services.SimpleBenchmarkProperty.UNIT;
import static pacr.webapp_backend.result_management.services.SimpleBenchmarkingResult.BENCHMARK_NAME;

public class StatisticalCalculatorTest {

    @Test
    void getQuantile_parameterIsNull_shouldReturnErrorCode() {
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

    @Test
    void significantChange_comparisonResultHasHighStandardDeviation_shouldNotSetPropertyResultSignificant() {
        BenchmarkProperty property = new BenchmarkProperty(PROPERTY_NAME, UNIT, ResultInterpretation.LESS_IS_BETTER);
        SimpleBenchmarkProperty resultOne = new SimpleBenchmarkProperty();

        BenchmarkPropertyResult propertyResult = new BenchmarkPropertyResult(resultOne, property);

        SimpleBenchmarkProperty resultTwo = new SimpleBenchmarkProperty();
        List<Double> measurements = Arrays.asList(5d, 17d);
        resultTwo.setResults(measurements);

        BenchmarkPropertyResult comparisonResult = new BenchmarkPropertyResult(resultTwo, property);

        StatisticalCalculator.compare(propertyResult, comparisonResult);

        assertFalse(propertyResult.isSignificant());
    }
}
