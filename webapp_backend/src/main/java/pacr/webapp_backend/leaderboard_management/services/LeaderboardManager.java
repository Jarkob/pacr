package pacr.webapp_backend.leaderboard_management.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pacr.webapp_backend.shared.ILeaderboard;
import pacr.webapp_backend.shared.ILeaderboardGetter;
import pacr.webapp_backend.shared.IObserver;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Manages the different leaderboard services.
 *
 * Implements {@link ILeaderboardGetter} to give other components access to leaderboards.
 * Implements {@link IObserver} to get notified, when new benchmarking results arrive.
 *
 */

@Controller
public class LeaderboardManager implements ILeaderboardGetter, IObserver {

    private ILeaderboardAccess leaderboardAccess;
    private LeaderboardCalculator leaderboardCalculator;

    @Autowired
    public LeaderboardManager (@NotNull ILeaderboardAccess leaderboardAccess,
                               @NotNull LeaderboardCalculator leaderboardCalculator) {
        Objects.requireNonNull(leaderboardAccess, "The leaderboard access instance must not be null.");
        Objects.requireNonNull(leaderboardCalculator, "The leaderboard calculator instance must not be null");

        this.leaderboardAccess = leaderboardAccess;
        this.leaderboardCalculator = leaderboardCalculator;
    }


    @Override
    public ILeaderboard getLeaderboard(String benchmarkName, String benchmarkPropertyName) {
        return leaderboardAccess.findByBenchmarkNameAndAndBenchmarkPropertyName(benchmarkName, benchmarkPropertyName);
    }

    @Override
    public Collection<ILeaderboard> getAllLeaderboards() {
        return new ArrayList<>(leaderboardAccess.findAll());
    }


    @Override
    public void update() {
        this.leaderboardCalculator.calculateAllLeaderboards();
    }

}
