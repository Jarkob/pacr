package pacr.webapp_backend.leaderboard_management.services;

import pacr.webapp_backend.leaderboard_management.Leaderboard;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * This interface is used for storing and accessing
 * objects, which are used and managed by the leaderboard component.
 *
 * @author Benedikt Hahn
 */
public interface ILeaderboardAccess {

    /**
     * @param benchmarkName The name of the benchmark, the requested leaderboard belongs to.
     * @param benchmarkPropertyName The name of the benchmark property, the requested leaderbooard belongs to.
     * @return the leaderboard with the given benchmark.
     */
    Leaderboard findByBenchmarkNameAndAndBenchmarkPropertyName(String benchmarkName, String benchmarkPropertyName);

    /**
     * @return a collection of all currently stored leaderboards.
     */
    Collection<Leaderboard> findAll();

    /**
     * Stores the given leaderboard in the database, overwriting an older version, if necessary.
     *
     * @param leaderboard the leaderboard, which will be stored in the database.
     */
    void storeLeaderboard(@NotNull Leaderboard leaderboard);

}
