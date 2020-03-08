package pacr.webapp_backend.shared;

import java.util.Collection;

/**
 * Contains methods for providing access to leaderboards.
 */
public interface ILeaderboardGetter {

    /**
     * @param benchmarkName the name of the benchmark.
     * @return the leaderboard, which belongs to the specified benchmark.
     */
    ILeaderboard getLeaderboard(String benchmarkName, String j);

    /**
     * @return a collection of all existing leaderboards.
     */
    Collection<ILeaderboard> getAllLeaderboards();
}
