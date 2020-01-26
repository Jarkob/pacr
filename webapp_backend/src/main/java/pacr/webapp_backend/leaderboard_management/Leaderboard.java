package pacr.webapp_backend.leaderboard_management;

import pacr.webapp_backend.shared.ILeaderboard;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

//TODO
@Entity
public class Leaderboard extends ILeaderboard {

    @Id
    String benchmarkName;

    /**
     * Empty constructor for JPA.
     */
    public Leaderboard(){

    }

    /**
     * Generates a new leaderboard with a benchmark name.
     * @param benchmarkName the name of the benchmark, this leaderboard belongs to.
     */
    Leaderboard(String benchmarkName) {
        this.benchmarkName = benchmarkName;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }
}
