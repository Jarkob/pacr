package pacr.webapp_backend.result_management;

/**
 * Represents a group of benchmarks.
 */
class BenchmarkGroup {
    private int id;
    private String name;

    /**
     * Creates a new BenchmarkGroup with a name.
     * @param name the name.
     */
    BenchmarkGroup(String name) {
        this.name = name;
    }

    /**
     * Gets the unique id of this group.
     * @return the id.
     */
    int getId() {
        return id;
    }

    /**
     * Gets the name of this group.
     * @return the name.
     */
    String getName() {
        return name;
    }

    /**
     * Sets the name of this group to a new name.
     * @param name the new name.
     */
    void setName(String name) {
        this.name = name;
    }
}
