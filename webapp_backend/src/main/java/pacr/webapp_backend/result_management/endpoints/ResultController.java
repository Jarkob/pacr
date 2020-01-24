package pacr.webapp_backend.result_management.endpoints;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.result_management.OutputBenchmarkingResult;
import pacr.webapp_backend.result_management.services.DiagramOutputResult;
import pacr.webapp_backend.result_management.services.ResultGetter;
import pacr.webapp_backend.result_management.services.ResultManager;
import pacr.webapp_backend.shared.IAuthenticator;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides endpoints for getting benchmarking results and deleting them.
 */
@RestController
public class ResultController {

    private IAuthenticator authenticator;
    private ResultGetter resultGetter;
    private ResultManager resultManager;

    /**
     * Creates a new ResultController.
     * @param authenticator the authenticator for authentication of jwts.
     * @param resultGetter access to results.
     * @param resultManager access to deleting results.
     */
    public ResultController(IAuthenticator authenticator, ResultGetter resultGetter, ResultManager resultManager) {
        this.authenticator = authenticator;
        this.resultGetter = resultGetter;
        this.resultManager = resultManager;
    }

    /**
     * Gets all benchmarking results of a specific repository.
     * @param repositoryId the id of the repository.
     * @return the benchmarking results.
     */
    @GetMapping("/results/repository/{repositoryId}")
    public Map<String, DiagramOutputResult> getBenchmarkingResultsFromRepository(@PathVariable int repositoryId) {
        return resultGetter.getRepositoryResults(repositoryId);
    }

    /**
     * Gets all benchmarking results of a specific branch.
     * @param repositoryId the id of the repository.
     * @param branch the name of the branch. Cannot be null, empty or blank.
     * @return the benchmarking results.
     * @throws NotFoundException if no repository with the given id and/or no branch with the given name could be found.
     */
    @GetMapping("/results/repository/{repositoryId}/{branch}")
    public Map<String, DiagramOutputResult> getBenchmarkingResultsFromBranch(
            @PathVariable int repositoryId, @NotNull @PathVariable String branch) throws NotFoundException {
        if (!StringUtils.hasText(branch)) {
            throw new IllegalArgumentException("branch cannot be null, empty or blank");
        }

        return resultGetter.getBranchResults(repositoryId, branch);
    }

    /**
     * Gets the benchmarking result of a specific commit.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     * @return the benchmarking result.
     * @throws NotFoundException if no commit with this hash or no result for this commit could be found.
     */
    @GetMapping("/results/commit/{commitHash}")
    public OutputBenchmarkingResult getBenchmarkingResultForCommit(@NotNull @PathVariable String commitHash)
            throws NotFoundException {
        if (!StringUtils.hasText(commitHash)) {
            throw new IllegalArgumentException("commit hash cannot be null, empty or blank");
        }

        return resultGetter.getCommitResult(commitHash);
    }

    /**
     * Gets the benchmarking results of all commits, but only for the specified benchmark.
     * @param benchmarkId the id of the benchmark.
     * @return the benchmarking results of one benchmark.
     */
    @GetMapping("/results/benchmark/{benchmarkId}")
    public Map<String, DiagramOutputResult> getBenchmarkingResultsForBenchmark(@PathVariable int benchmarkId) {
        return resultGetter.getBenchmarkResults(benchmarkId);
    }

    /**
     * Gets the benchmarking results of all commits of a repository, but only for the specified benchmark.
     * @param repositoryId the id of the repository.
     * @param benchmarkId the id of the benchmark.
     * @return the benchmarking results of one benchmark.
     */
    @GetMapping("/results/benchmark/{benchmarkId}/{repositoryId}")
    public Map<String, DiagramOutputResult> getResultsForRepositoryAndBenchmark(@PathVariable int benchmarkId,
                                                                                    @PathVariable int repositoryId) {
        return resultGetter.getBenchmarkResults(repositoryId, benchmarkId);
    }

    /**
     * Gets up to 100 of the newest saved benchmarking results.
     * @return the newest results.
     */
    @GetMapping("/history")
    public List<OutputBenchmarkingResult> getNewBenchmarkingResults() {
        return resultGetter.getNewestResults();
    }

    /**
     * Deletes any benchmarking result of the commit. This is a privileged method.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     * @param jwt the json web token for authentication. Cannot be null, empty or blank.
     * @return HTTP code 200 (ok) if the result was deleted or nonexistent in the first place. HTTP code 401
     *         (unauthorized) if the given jwt was invalid.
     */
    @DeleteMapping("/results/commit/{commitHash}")
    public ResponseEntity<Object> deleteBenchmarkingResult(@NotNull @PathVariable String commitHash,
                                                           @NotNull @RequestHeader(name = "jwt") String jwt) {
        if (!StringUtils.hasText(commitHash) || !StringUtils.hasText(jwt)) {
            throw new IllegalArgumentException("commit hash or jwt cannot be null, empty or blank");
        }

        if (authenticator.authenticate(jwt)) {
            resultManager.deleteBenchmarkingResults(commitHash);

            return ResponseEntity.ok().build();
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
