package pacr.webapp_backend.result_management.endpoints;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.result_management.services.Benchmark;
import pacr.webapp_backend.result_management.services.BenchmarkGroup;
import pacr.webapp_backend.result_management.services.BenchmarkManager;
import pacr.webapp_backend.shared.IAuthenticator;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Provides endpoints for getting benchmark metadata and changing it (if the user is authenticated).
 */
@RestController
public class BenchmarkController {

    private IAuthenticator authenticator;
    private BenchmarkManager benchmarkManager;

    /**
     * Creates a new BenchmarkController.
     * @param authenticator the authenticator to authenticate jwts.
     * @param benchmarkManager the benchmark manager.
     */
    public BenchmarkController(final IAuthenticator authenticator, final BenchmarkManager benchmarkManager) {
        this.authenticator = authenticator;
        this.benchmarkManager = benchmarkManager;
    }

    /**
     * Gets all benchmarks and their metadata.
     * @return the benchmarks.
     */
    @GetMapping("/benchmarks")
    public Collection<Benchmark> getAllBenchmarks() {
        return benchmarkManager.getAllBenchmarks();
    }

    /**
     * Gets all benchmarks of a group.
     * @param groupId the id of the group.
     * @return the benchmarks of the group or with no group.
     */
    @GetMapping("/benchmarks/{groupId}")
    public Collection<Benchmark> getBenchmarksByGroup(@PathVariable final int groupId) {
        return benchmarkManager.getBenchmarksByGroup(groupId);
    }

    /**
     * Gets all benchmark groups and their metadata.
     * @return the groups.
     */
    @GetMapping("/groups")
    public Collection<BenchmarkGroup> getAllGroups() {
        return benchmarkManager.getAllGroups();
    }

    /**
     * Updates the benchmark with the given values. This is a privileged method.
     * @param benchmark the updated benchmark. Attributes must be valid.
     * @param jwt the json web token for authentication. Cannot be null, empty or blank.
     * @return HTTP code 200 (ok) if the benchmark was updated. HTTP code 404 (not found) if no benchmark and/or no
     *         group with the given id could be found. HTTP code 401 (unauthorized) if the given jwt was invalid.
     */
    @PutMapping("/benchmark")
    public ResponseEntity<Object> updateBenchmark(@RequestBody final BenchmarkInput benchmark,
                                                  @NotNull @RequestHeader(name = "jwt") final String jwt) {
        if (!(benchmark.validate() && StringUtils.hasText(jwt))) {
            throw new IllegalArgumentException("jwt or input of benchmark cannot be null, empty or blank");
        }

        if (authenticator.authenticate(jwt)) {
            try {
                benchmarkManager.updateBenchmark(benchmark.getId(), benchmark.getCustomName(),
                        benchmark.getDescription(), benchmark.getGroupId());
            } catch (final NoSuchElementException e) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().build();
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates a new benchmark group with the given name. This is a privileged method.
     * @param name the name of the group. Cannot be null, empty or blank.
     * @param jwt the json web token for authentication. Cannot be null, empty or blank.
     * @return HTTP code 200 (ok) if the group was added. HTTP code 401 (unauthorized) if the given jwt was invalid.
     */
    @PostMapping("/group")
    public ResponseEntity<Object> addGroup(@NotNull @RequestBody final String name,
                                           @NotNull @RequestHeader(name = "jwt") final String jwt) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(jwt)) {
            throw new IllegalArgumentException("name and jwt cannot be null, empty or blank");
        }

        if (authenticator.authenticate(jwt)) {
            benchmarkManager.addGroup(name);

            return ResponseEntity.ok().build();
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Updates the group with the given name. This is a privileged method.
     * @param groupId the id of the group.
     * @param name the new name of the group. Cannot be null, empty or blank.
     * @param jwt the json web token for authentication. Cannot be null, empty or blank.
     * @return HTTP code 200 (ok) if the group was updated. HTTP code 404 (not found) if no group with the given id
     *         could be found. HTTP code 401 (unauthorized) if the given jwt was invalid.
     */
    @PutMapping("/group/{groupId}")
    public ResponseEntity<Object> updateGroup(@PathVariable final int groupId, @NotNull @RequestBody final String name,
                                              @NotNull @RequestHeader(name = "jwt") final String jwt) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(jwt)) {
            throw new IllegalArgumentException("name and jwt cannot be null, empty or blank");
        }

        if (authenticator.authenticate(jwt)) {
            try {
                benchmarkManager.updateGroup(groupId, name);
            } catch (final NoSuchElementException e) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().build();
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Deletes the benchmark group. Any associated benchmarks get set to no group.
     * @param groupId the id of the group.
     * @param jwt the json web token for authentication. Cannot be null, empty or blank.
     * @return HTTP code 200 (ok) if the group was deleted. HTTP code 404 (not found) if no group with the given id
     *         could be found. HTTP code 401 (unauthorized) if the given jwt was invalid.
     */
    @DeleteMapping("/group/{groupId}")
    public ResponseEntity<Object> deleteGroup(@PathVariable final int groupId,
                                              @NotNull @RequestHeader(name = "jwt") final String jwt) {
        if (!StringUtils.hasText(jwt)) {
            throw new IllegalArgumentException("jwt cannot be null, empty or blank");
        }

        if (authenticator.authenticate(jwt)) {
            try {
                benchmarkManager.deleteGroup(groupId);
            } catch (final NoSuchElementException e) {
                return ResponseEntity.notFound().build();
            } catch (final IllegalAccessException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }

            return ResponseEntity.ok().build();
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
