package pacr.webapp_backend.result_management;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pacr.webapp_backend.shared.ResultInterpretation;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BenchmarkPropertyResultTest {

    private static final String commitHash = "test123";

    private static BenchmarkPropertyResult propertyResultEven;
    private static BenchmarkPropertyResult propertyResultOdd;

    private static final double DELTA = 0.01;
    private static final Double[] RESULTS_EVEN = { 3d, 4d, 5d, 9d };
    private static final double MEAN_EVEN = 5.25;
    private static final double LOWER_QUARTILE_EVEN = 3.5;
    private static final double MEDIAN_EVEN = 4.5;
    private static final double UPPER_QUARTILE_EVEN = 7;
    private static final double STANDARD_DEVIATION_EVEN = 2.28;

    private static final Double[] RESULTS_ODD = { 3d, 4d, 9d };
    private static final double MEAN_ODD = 5.33;
    private static final double LOWER_QUARTILE_ODD = 3;
    private static final double MEDIAN_ODD = 4;
    private static final double UPPER_QUARTILE_ODD = 9;
    private static final double STANDARD_DEVIATION_ODD = 2.62;

    /**
     * Sets up two property results. One with an odd and one with an even number of measurements. This distinction is
     * important because the method to calculate the median, etc. differs slightly in these two cases.
     */
    @BeforeAll
    public static void setUp() {
        Benchmark benchmark = new Benchmark("stub");
        ResultInterpretation lessIsBetter = ResultInterpretation.LESS_IS_BETTER;
        BenchmarkProperty property = new BenchmarkProperty("stub", "stub", lessIsBetter, benchmark);
        propertyResultEven = new BenchmarkPropertyResult(Arrays.asList(RESULTS_EVEN), property);
        propertyResultOdd = new BenchmarkPropertyResult(Arrays.asList(RESULTS_ODD), property);
    }

    /**
     * Tests if the mean of the measurements was properly calculated.
     */
    @Test
    public void getMean_measurementsAreEntered_ShouldReturnMean() {
        assertEquals(MEAN_EVEN, propertyResultEven.getMean(), DELTA);
        assertEquals(MEAN_ODD, propertyResultOdd.getMean(), DELTA);
    }

    /**
     * Tests if the lower quartile of the measurements was properly calculated.
     */
    @Test
    public void getLowerQuartile_measurementsAreEntered_ShouldReturnLowerQuartile() {
        assertEquals(LOWER_QUARTILE_EVEN, propertyResultEven.getLowerQuartile(), DELTA);
        assertEquals(LOWER_QUARTILE_ODD, propertyResultOdd.getLowerQuartile(), DELTA);
    }

    /**
     * Tests if the median of the measurements was properly calculated.
     */
    @Test
    public void getMedian_measurementsAreEntered_ShouldReturnMedian() {
        assertEquals(MEDIAN_EVEN, propertyResultEven.getMedian(), DELTA);
        assertEquals(MEDIAN_ODD, propertyResultOdd.getMedian(), DELTA);
    }

    /**
     * Tests if the upper quartile of the measurements was properly calculated.
     */
    @Test
    public void getUpperQuartile_measurementsAreEntered_ShouldReturnUpperQuartile() {
        assertEquals(UPPER_QUARTILE_EVEN, propertyResultEven.getUpperQuartile(), DELTA);
        assertEquals(UPPER_QUARTILE_ODD, propertyResultOdd.getUpperQuartile(), DELTA);
    }

    /**
     * Tests if the standard deviation of the measurements was properly calculated.
     */
    @Test
    public void getStandardDeviation_measurementsAreEntered_ShouldReturnStandardDeviation() {
        assertEquals(STANDARD_DEVIATION_EVEN, propertyResultEven.getStandardDeviation(), DELTA);
        assertEquals(STANDARD_DEVIATION_ODD, propertyResultOdd.getStandardDeviation(), DELTA);
    }
}
