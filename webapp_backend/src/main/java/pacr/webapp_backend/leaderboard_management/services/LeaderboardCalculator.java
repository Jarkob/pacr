package pacr.webapp_backend.leaderboard_management.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pacr.webapp_backend.leaderboard_management.Leaderboard;
import pacr.webapp_backend.shared.IBenchmark;
import pacr.webapp_backend.shared.IBenchmarkGetter;
import pacr.webapp_backend.shared.IBenchmarkingResult;
import pacr.webapp_backend.shared.INewestResult;
import pacr.webapp_backend.shared.IRepository;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Calculates the new order of repositories in dashboards
 * and generates change messages for the calculated leaderboards.
 *
 * @author Benedikt Hahn
 */

@Service
public class LeaderboardCalculator {

    private ILeaderboardAccess leaderboardAccess;
    private INewestResult newestResult;

    private IBenchmarkGetter benchmarkGetter;

    private static final Logger LOGGER = LogManager.getLogger(LeaderboardCalculator.class);

    @Autowired
    public LeaderboardCalculator(@NotNull ILeaderboardAccess leaderboardAccess, @NotNull INewestResult newestResult) {
        Objects.requireNonNull(leaderboardAccess, "The ILeaderboardAccess instance must not be null.");
        Objects.requireNonNull(newestResult, "The INewestResult instance must not be null.");

        this.leaderboardAccess = leaderboardAccess;
        this.newestResult = newestResult;
    }


    /**
     * Calculates the leaderboard for the benchmark with the given name.
     * @param benchmarkName The name of the benchmark the leaderboard, which should be calculated belongs to.
     * @param benchmarkPropertyName The name of the property of the benchmark the leaderboard belongs to.
     * @return The calculated leaderboard.
     */
    public Leaderboard calculateLeaderboard(String benchmarkName, String benchmarkPropertyName) {
        Leaderboard oldLeaderboard =
                leaderboardAccess.findByBenchmarkNameAndAndBenchmarkPropertyName(benchmarkName, benchmarkPropertyName);

        Collection<IRepository> allRepositories = getAllRepositories();
        Objects.requireNonNull(allRepositories);

        List<IBenchmark> relevantBenchmarks = new ArrayList<>();

        Map<IBenchmark, IRepository> resultToRepositoryMapping = new HashMap<>();

        for (IRepository repository : allRepositories) {
            int id = repository.getId();
            IBenchmarkingResult resultOfRepository = newestResult.getNewestResult(id);

            if (resultOfRepository.getBenchmarks().containsKey(benchmarkName)) {
                IBenchmark benchmark = resultOfRepository.getBenchmarks().get(benchmarkName);
                relevantBenchmarks.add(benchmark);
                resultToRepositoryMapping.put(benchmark, repository);
            }
        }


        relevantBenchmarks.sort(new BenchmarkComparator(benchmarkPropertyName));


        List<IRepository> orderedRepositories = new ArrayList<>();

        for (IBenchmark relevantBenchmark : relevantBenchmarks) {
            orderedRepositories.add(resultToRepositoryMapping.get(relevantBenchmark));
        }

        Leaderboard newLeaderboard = new Leaderboard(benchmarkName, benchmarkPropertyName, orderedRepositories);

        newLeaderboard.setChangeMessages(createChangeMessages(oldLeaderboard, newLeaderboard));

        leaderboardAccess.storeLeaderboard(newLeaderboard);

        return newLeaderboard;
    }

    /**
     * Calculates the leaderboArd for all current benchmarks.
     * @return a collection of all calculated leaderboards.
     */
    public Collection<Leaderboard> calculateAllLeaderboards() {
        Map<String, Collection<String>> benchmarkMapping = benchmarkGetter.getAllBenchmarkNamesWithPropertyNames();

        Collection<Leaderboard> calculatedLeaderboards = new ArrayList<>();

        for (String benchmarkName : benchmarkMapping.keySet()) {
            for (String propertyName : benchmarkMapping.get(benchmarkName)) {
                calculatedLeaderboards.add(calculateLeaderboard(benchmarkName, propertyName));
            }
        }

        return calculatedLeaderboards;
    }

    private List<String> createChangeMessages(Leaderboard oldLeaderboard, Leaderboard newLeaderboard) {
        return null;
    }

    //TODO IDK HOW
    private Collection<IRepository> getAllRepositories() {
        return null;
    }


}
