package pacr.webapp_backend.result_management.endpoints;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.result_management.services.CommitHistoryItem;
import pacr.webapp_backend.result_management.services.OutputBenchmarkingResult;
import pacr.webapp_backend.result_management.services.DiagramOutputResult;
import pacr.webapp_backend.result_management.services.ResultGetter;
import pacr.webapp_backend.result_management.services.ResultManager;
import pacr.webapp_backend.shared.IAuthenticator;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Provides endpoints for getting benchmarking results and deleting them.
 */
@RestController
public class ResultController {

    private static final int DIAGRAM_PAGE_SIZE = 200;
    private static final int HISTORY_PAGE_SIZE = 50;
    private static final int BENCHMARKING_RESULT_PAGE_SIZE = 10;

    private final IAuthenticator authenticator;
    private final ResultGetter resultGetter;
    private final ResultManager resultManager;

    /**
     * Creates a new ResultController.
     * @param authenticator the authenticator for authentication of jwts.
     * @param resultGetter access to results.
     * @param resultManager access to deleting results.
     */
    public ResultController(final IAuthenticator authenticator, final ResultGetter resultGetter, final ResultManager resultManager) {
        this.authenticator = authenticator;
        this.resultGetter = resultGetter;
        this.resultManager = resultManager;
    }

    /**
     * Gets the benchmarking result of a specific commit.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     * @return the benchmarking result.
     */
    @GetMapping("/results/commit/{commitHash}")
    public OutputBenchmarkingResult getBenchmarkingResultForCommit(@NotNull @PathVariable final String commitHash) {
        if (!StringUtils.hasText(commitHash)) {
            throw new IllegalArgumentException("commit hash cannot be null, empty or blank");
        }

        return resultGetter.getCommitResult(commitHash);
    }

    /**
     * Gets the benchmarking results of all commits of a repository, but only for the specified benchmark.
     * TODO is this used by the frontend?
     * @param repositoryId the id of the repository.
     * @param benchmarkId the id of the benchmark.
     * @return the benchmarking results of one benchmark.
     */
    @GetMapping("/results/benchmark/{benchmarkId}/{repositoryId}")
    public Map<String, DiagramOutputResult> getResultsForRepositoryAndBenchmark(@PathVariable final int benchmarkId,
                                                                                    @PathVariable final int repositoryId) {
        return resultGetter.getBenchmarkResults(repositoryId, benchmarkId);
    }

    /**
     * Gets a page of the benchmarking results of a repository.
     * @param repositoryId the id of the repository.
     * @param pageable the requested page and sort (sorted by commit date by default).
     * @return the page of detailed results.
     */
    @GetMapping("/results/pageable/repository/{repositoryId}")
    public Page<OutputBenchmarkingResult> getResultsForRepository(@PathVariable final int repositoryId,
                                              @PageableDefault(size = BENCHMARKING_RESULT_PAGE_SIZE,
                                                      page = 0, sort = "commitDate") final Pageable pageable) {
        return resultGetter.getFullRepositoryResults(repositoryId, pageable);
    }

    /**
     * Gets the benchmarking results of a branch in a defined time frame (by commit date), but only for the specified
     * benchmark.
     * @param benchmarkId the id of the benchmark.
     * @param repositoryId the id of the repository.
     * @param branch the name of the branch. Cannot be {@code null}.
     * @param startTimeStamp the start time of the results, as the number of seconds since 1970/01/01.
     * @param endTimeStamp the end time of the results, as the number of seconds since 1970/01/01.
     * @return the benchmarking results of one benchmark.
     */
    @GetMapping("/results/pageable/benchmark/{benchmarkId}/{repositoryId}/{branch}/{startTimeStamp}/{endTimeStamp}")
    public Map<String, DiagramOutputResult> getResultPageForBranchAndBenchmark(@PathVariable int benchmarkId,
                                                   @PathVariable int repositoryId,
                                                   @NotNull @PathVariable String branch,
                                                   @PathVariable long startTimeStamp, @PathVariable long endTimeStamp) {
        Objects.requireNonNull(branch);

        ZoneOffset currentOffset = ZoneOffset.systemDefault().getRules().getOffset(Instant.now());
        LocalDateTime start = LocalDateTime.ofEpochSecond(startTimeStamp, 0, currentOffset);
        LocalDateTime end = LocalDateTime.ofEpochSecond(endTimeStamp, 0, currentOffset);

        return resultGetter.getBenchmarkResultsSubset(benchmarkId, repositoryId, branch, start, end);
    }

    /**
     * Gets the newest saved benchmarking results.
     * @param pageable the requested page. Default page is 0 and default size is HISTORY_PAGE_SIZE.
     * @return the newest results.
     */
    @GetMapping("/history")
    public Page<CommitHistoryItem> getNewBenchmarkingResults(
            @PageableDefault(size = HISTORY_PAGE_SIZE, page = 0) final Pageable pageable) {
        return resultGetter.getNewestResults(pageable);
    }

    /**
     * Deletes any benchmarking result of the commit. This is a privileged method.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     * @param jwt the json web token for authentication. Cannot be null, empty or blank.
     * @return HTTP code 200 (ok) if the result was deleted or nonexistent in the first place. HTTP code 401
     *         (unauthorized) if the given jwt was invalid.
     */
    @DeleteMapping("/results/commit/{commitHash}")
    public ResponseEntity<Object> deleteBenchmarkingResult(@NotNull @PathVariable final String commitHash,
                                                           @NotNull @RequestHeader(name = "jwt") final String jwt) {
        if (!StringUtils.hasText(commitHash) || !StringUtils.hasText(jwt)) {
            throw new IllegalArgumentException("commit hash or jwt cannot be null, empty or blank");
        }

        if (authenticator.authenticate(jwt)) {
            resultManager.deleteBenchmarkingResults(Arrays.asList(commitHash));

            return ResponseEntity.ok().build();
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Gets all measurements that were made for a benchmark property for a commit.
     * @param commitHash the hash of the commit. Cannot be null, empty or blank.
     * @param benchmarkId the id of the benchmark of the property.
     * @param propertyName the name of the property. Cannot be null, empty or blank.
     * @return the measurements for this property and commit.
     */
    @GetMapping("/results/commit/{commitHash}/{benchmarkId}/{propertyName}")
    public List<Double> getMeasurementsOfPropertyForCommit(@NotNull @PathVariable final String commitHash,
                                                           @PathVariable final int benchmarkId,
                                                           @NotNull @PathVariable final String propertyName) {
        if (!StringUtils.hasText(commitHash) || !StringUtils.hasText(propertyName)) {
            throw new IllegalArgumentException("input cannot be null, empty or blank");
        }

        return resultGetter.getMeasurementsOfPropertyForCommit(commitHash, benchmarkId, propertyName);
    }
}
