package pacr.webapp_backend.leaderboard_management.services;

import org.springframework.stereotype.Controller;
import pacr.webapp_backend.shared.ILeaderboard;
import pacr.webapp_backend.shared.ILeaderboardGetter;

import java.util.Collection;

//TODO
@Controller
public class LeaderboardManager implements ILeaderboardGetter {


    @Override
    public ILeaderboard getLeaderboard(final String benchmarkName) {
        return null;
    }

    @Override
    public Collection<ILeaderboard> getAllLeaderboards() {
        return null;
    }
}
