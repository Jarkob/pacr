package pacr.webapp_backend.benchmarker_communication.services;

/**
 * Manages a collection of PACR-Benchmarkers.
 * Benchmarker can be marked free or occupied. The IBenchmarkerPool provides an interface
 * to get a free Benchmarker.
 */
public interface IBenchmarkerPool {

    /**
     * @return whether there is a free Benchmarker available.
     */
    boolean hasFreeBenchmarkers();

    /**
     * @return the address of a free Benchmarker in the pool. If there is no free Benchmarker
     * null is returned.
     */
    String getFreeBenchmarker();

    /**
     * Marks a Benchmarker as free.
     * @param address the address of the Benchmarker.
     */
    void freeBenchmarker(String address);

    /**
     * Marks a Benchmarker as occupied.
     * @param address the address of the Benchmarker.
     */
    void occupyBenchmarker(String address);

}
