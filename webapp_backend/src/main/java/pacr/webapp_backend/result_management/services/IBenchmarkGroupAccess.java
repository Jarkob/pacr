package pacr.webapp_backend.result_management.services;

import java.util.Collection;

/**
 * Saves benchmark group objects in the database and retrieves them.
 */
public interface IBenchmarkGroupAccess {

    /**
     * Gets all saved benchmark groups.
     * @return all groups.
     */
    Collection<BenchmarkGroup> getAllGroups();

    /**
     * Gets the benchmark group with the entered id. If no such group is saved, returns null.
     * @param id the group id.
     * @return the group.
     */
    BenchmarkGroup getBenchmarkGroup(int id);

    /**
     * Saves the given group or updates it in the database.
     * @param group the group
     * @return the groups id.
     */
    int saveBenchmarkGroup(BenchmarkGroup group);

    /**
     * Deletes the given group in the database.
     * @param group the group.
     */
    void deleteGroup(BenchmarkGroup group);
}
