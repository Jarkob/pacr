package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.leaderboard_management.Leaderboard;
import pacr.webapp_backend.leaderboard_management.services.ILeaderboardAccess;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * This interface allows access to a database regarding entities,
 * which are managed in the leaderboard_management component.
 * For this, it contains jpa auto-generated methods and default implementations.
 *
 * @author Benedikt Hahn
 */
@Component
public interface LeaderboardDB extends CrudRepository<Leaderboard, String>, ILeaderboardAccess {

    @Override
    Leaderboard findByBenchmarkNameAndAndBenchmarkPropertyName(String benchmarkName, String benchmarkPropertyName);

    @Override
    Collection<Leaderboard> findAll();


    @Override
    default void storeLeaderboard(@NotNull Leaderboard leaderboard) {
        this.save(leaderboard);
    }

}
