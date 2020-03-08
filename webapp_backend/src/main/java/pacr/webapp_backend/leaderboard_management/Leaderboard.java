package pacr.webapp_backend.leaderboard_management;

import lombok.NoArgsConstructor;
import lombok.Getter;
import pacr.webapp_backend.shared.ILeaderboard;
import pacr.webapp_backend.shared.IRepository;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

/**
 * Instances of this class model leaderboards, which contain information
 * about the performance of different repositories compared to each other
 * in a single benchmark.
 *
 * @author Benedikt Hahn
 */
@Entity(name = "Leaderboard")
@Table(name = "leaderboard")
@NoArgsConstructor
@Getter
public class Leaderboard extends ILeaderboard {

    String benchmarkName;
    String benchmarkPropertyName;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    List<IRepository> repositoryIDs;

    private transient List<String> changeMessages;

    /**
     * Generates a new leaderboard with a benchmark name.
     * @param benchmarkName the name of the benchmark, this leaderboard belongs to.
     * @param benchmarkPropertyName the name of the benchmark property, this leaderboard belongs to.
     * @param repositoryIDs the ordered list of repository ids , this leaderboard holds.
     */
    public Leaderboard(String benchmarkName, String benchmarkPropertyName, List<IRepository> repositoryIDs) {
        this.benchmarkName = benchmarkName;
        this.benchmarkPropertyName = benchmarkPropertyName;
        this.repositoryIDs = List.copyOf(repositoryIDs);
    }


    @Override
    public int getId() {
        return this.id;
    }

    /**
     * @return a copy of the ordered list of repositories in this leaderboard.
     */
    public List<IRepository> getRepositoryIDs() {
        return List.copyOf(repositoryIDs);
    }


    /**
     * @return The current change messages of this leaderboard.
     */
    public List<String> getChangeMessages() {
        return List.copyOf(changeMessages);
    }


    /**
     * Sets the change messages of this leaderboard to the given messages.
     * @param messages The new change messages.
     */
    public void setChangeMessages(List<String> messages) {
        this.changeMessages = List.copyOf(messages);
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        Leaderboard otherLeaderboard = (Leaderboard) o;

        if ((this.benchmarkName == null && otherLeaderboard.benchmarkName != null)
            || (this.benchmarkName != null && otherLeaderboard.benchmarkName == null)) {
            return false;
        }

        if (this.benchmarkPropertyName == null && otherLeaderboard.benchmarkPropertyName != null
                || this.benchmarkPropertyName != null && otherLeaderboard.benchmarkPropertyName == null) {
            return false;
        }

        if (this.repositoryIDs == null && otherLeaderboard.repositoryIDs != null
                || this.repositoryIDs != null && otherLeaderboard.repositoryIDs == null) {
            return false;
        }

        if (this.benchmarkName != otherLeaderboard.benchmarkName
                && !(this.benchmarkName.equals(otherLeaderboard.benchmarkName))) {
            return false;
        }

        if (this.benchmarkPropertyName != otherLeaderboard.benchmarkPropertyName
                && !(this.benchmarkPropertyName.equals(otherLeaderboard.benchmarkPropertyName))) {
            return false;
        }

        if (this.id != otherLeaderboard.id) {
            return false;
        }

        if (!(this.repositoryIDs == otherLeaderboard.repositoryIDs)
                && this.repositoryIDs.size() != otherLeaderboard.repositoryIDs.size()) {
            return false;
        }

        if (this.repositoryIDs == otherLeaderboard.repositoryIDs) {
             return true;
        }


        for (int i = 0; i  < this.repositoryIDs.size(); i++) {
            if (!this.repositoryIDs.get(i).equals(otherLeaderboard.repositoryIDs.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(benchmarkName, benchmarkPropertyName, repositoryIDs, changeMessages, id);
    }
}
