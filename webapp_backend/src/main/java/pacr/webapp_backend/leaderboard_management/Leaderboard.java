package pacr.webapp_backend.leaderboard_management;

import lombok.NoArgsConstructor;
import pacr.webapp_backend.shared.ILeaderboard;

import javax.persistence.Entity;
import javax.persistence.Id;

//TODO
@Entity
@NoArgsConstructor
public class Leaderboard extends ILeaderboard {

    @Id
    String benchmarkName;

    /**
     * Generates a new leaderboard with a benchmark name.
     * @param benchmarkName the name of the benchmark, this leaderboard belongs to.
     */
    Leaderboard(final String benchmarkName) {
        this.benchmarkName = benchmarkName;
    }

    @Override
    public boolean equals(final Object o) {
        return true;
    }
}
