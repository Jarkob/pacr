package pacr.webapp_backend.result_management;

import pacr.webapp_backend.shared.ResultInterpretation;

/**
 * Represents one property of a benchmark that is measured.
 * This entity is saved in the database.
 */
class BenchmarkProperty {
    private int id;
    private String name;
    private String unit;
    private ResultInterpretation interpretation;
    private Benchmark benchmark;

    /**
     * Creates a BenchmarkProperty with a name, a unit, an interpretation and the corresponding benchmark.
     * This constructor does not add this property to the benchmark.
     *
     * @param name the name;
     * @param unit the unit;
     * @param interpretation the interpretation;
     * @param benchmark the corresponding benchmark;
     */
    BenchmarkProperty(String name, String unit, ResultInterpretation interpretation, Benchmark benchmark) {
        this.name = name;
        this.unit = unit;
        this.interpretation = interpretation;
        this.benchmark = benchmark;
    }

    /**
     * Gets the unique id of this property.
     * @return the id.
     */
    int getId() {
        return id;
    }

    /**
     * Gets the name of this property.
     * @return the name.
     */
    String getName() {
        return name;
    }

    /**
     * Gets the measured unit of this property.
     * @return the unit.
     */
    String getUnit() {
        return unit;
    }

    /**
     * Gets the interpretation (usually "MORE_IS_BETTER", "LESS_IS_BETTER" or "NEUTRAL") of this property.
     * @return the interpretation.
     */
    ResultInterpretation getInterpretation() {
        return interpretation;
    }

    /**
     * Gets the benchmark that this property is associated with. Does not guarantee that the given benchmark is also
     * associated with this property.
     * @return the benchmark.
     */
    Benchmark getBenchmark() {
        return benchmark;
    }
}
