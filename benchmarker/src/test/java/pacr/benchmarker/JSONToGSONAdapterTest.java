package pacr.benchmarker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pacr.benchmarker.services.JSONToGSONAdapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for JSONToGSONAdapter.
 *
 * @author Pavel Zwerschke
 */
public class JSONToGSONAdapterTest {

    private JSONToGSONAdapter adapter;

    @BeforeEach
    public void setUp() {
        adapter = new JSONToGSONAdapter();
    }

    @Test
    public void oneBenchmark() {
        String str = "{ \"TestBenchmark\": { \"Performance\": { \"results\": [ 1, 2, 3, 4 ], \"unit\": \"s\", \"resultInterpretation\": \"LESS_IS_BETTER\" } } }";

        String converted = adapter.convertJSONToGSON(str);

        String convertedNoWhitespace = converted.replaceAll("\\s+", "");
        String expected = "{\"benchmarks\":{\"TestBenchmark\":{\"properties\":{\"Performance\":{\"results\":[1,2,3,4],\"unit\":\"s\",\"resultInterpretation\":\"LESS_IS_BETTER\"}}}}}";
        assertEquals(expected, convertedNoWhitespace);
    }

    @Test
    public void twoBenchmarks() {
        String str = "{ \"TestBenchmark\": { \"Performance\": { \"results\": [ 1, 2, 3, 4 ], \"unit\": \"s\", \"resultInterpretation\": \"LESS_IS_BETTER\" } }, \"TestBenchmark2\": { \"Coolness\": { \"results\": [ 1, 2, 3, 4 ], \"unit\": \"coolPoints\", \"resultInterpretation\": \"MORE_IS_BETTER\" } } }";

        String converted = adapter.convertJSONToGSON(str);

        String convertedNoWhitespace = converted.replaceAll("\\s+", "");
        String expected = "{\"benchmarks\":{\"TestBenchmark\":{\"properties\":{\"Performance\":{\"results\":[1,2,3,4],\"unit\":\"s\",\"resultInterpretation\":\"LESS_IS_BETTER\"}}},\"TestBenchmark2\":{\"properties\":{\"Coolness\":{\"results\":[1,2,3,4],\"unit\":\"coolPoints\",\"resultInterpretation\":\"MORE_IS_BETTER\"}}}}}";
        assertEquals(expected, convertedNoWhitespace);
    }

    @Test
    public void errorOnSecondLevel() {
        String str = "{ \"TestBenchmark\": { \"Performance\": { \"error\": \"idk what happened\" } } }";

        String converted = adapter.convertJSONToGSON(str);

        String convertedNoWhitespace = converted.replaceAll("\\s+", "");
        String expected = "{\"benchmarks\":{\"TestBenchmark\":{\"properties\":{\"Performance\":{\"error\":\"idkwhathappened\"}}}}}";
        assertEquals(expected, convertedNoWhitespace);
    }

    @Test
    public void globalErrorTest() {
        String str = "{ \"error\": \"runner didn't work\" }";

        String converted = adapter.convertJSONToGSON(str);

        assertEquals(str, converted);
    }

}
