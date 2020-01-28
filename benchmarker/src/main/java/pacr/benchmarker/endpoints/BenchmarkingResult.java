package pacr.benchmarker.endpoints;

/**
 * @author Pavel Zwerschke
 */
public class BenchmarkingResult {

    private String result;

    private int executionTime;

    public BenchmarkingResult() {
    }

    public BenchmarkingResult(String result, int executionTime) {
        this.result = result;
        this.executionTime = executionTime;
    }

    public String getResult() {
        return result;
    }

    public int getExecutionTime() {
        return executionTime;
    }

}
