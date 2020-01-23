package pacr.webapp_backend.result_management.endpoints;

import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.result_management.Benchmark;
import pacr.webapp_backend.result_management.BenchmarkGroup;
import pacr.webapp_backend.result_management.services.BenchmarkManager;
import pacr.webapp_backend.shared.IAuthenticator;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Objects;

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
    public BenchmarkController(IAuthenticator authenticator, BenchmarkManager benchmarkManager) {
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
     * Gets all benchmark groups and their metadata.
     * @return the groups.
     */
    @GetMapping("/groups")
    public Collection<BenchmarkGroup> getAllGroups() {
        return benchmarkManager.getAllGroups();
    }

    /**
     * Updates the benchmark with the given values. This is a privileged method.
     * @param benchmarkId the id of the benchmark.
     * @param name the new name of the benchmark. Cannot be null, empty or blank.
     * @param description the new description of the benchmark. Cannot be null, empty or blank.
     * @param groupId the group id of the new group of the benchmark. -1 implies this benchmark has not group.
     * @param jwt the json web token for authentication. Cannot be null, empty or blank.
     * @return HTTP code 200 (ok) if the benchmark was updated. HTTP code 404 (not found) if no benchmark and/or no
     *         group with the given id could be found. HTTP code 401 (unauthorized) if the given jwt was invalid.
     */
    @PutMapping("/benchmark/{benchmarkId}/{name}/{description}/{groupId}")
    public ResponseEntity<Object> updateBenchmark(@PathVariable int benchmarkId, @NotNull @PathVariable String name,
                                                  @NotNull @PathVariable String description, @PathVariable int groupId,
                                                  @NotNull @RequestHeader(name = "jwt") String jwt) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(description) || !StringUtils.hasText(jwt)) {
            throw new IllegalArgumentException("name, description and jwt cannot be null, empty or blank");
        }

        if (authenticator.authenticate(jwt)) {
            try {
                benchmarkManager.updateBenchmark(benchmarkId, name, description, groupId);
            } catch (NotFoundException e) {
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
    @PostMapping("/benchmark/{name}")
    public ResponseEntity<Object> addGroup(@NotNull @PathVariable String name,
                                           @NotNull @RequestHeader(name = "jwt") String jwt) {
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
    @PostMapping("/benchmark/{groupId}/{name}")
    public ResponseEntity<Object> updateGroup(@PathVariable int groupId, @NotNull @PathVariable String name,
                            @NotNull @RequestHeader(name = "jwt") String jwt) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(jwt)) {
            throw new IllegalArgumentException("name and jwt cannot be null, empty or blank");
        }

        if (authenticator.authenticate(jwt)) {
            try {
                benchmarkManager.updateGroup(groupId, name);
            } catch (NotFoundException e) {
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
    @DeleteMapping("/benchmark/{groupId}")
    public ResponseEntity<Object> deleteGroup(@PathVariable int groupId,
                                              @NotNull @RequestHeader(name = "jwt") String jwt) {
        if (!StringUtils.hasText(jwt)) {
            throw new IllegalArgumentException("jwt cannot be null, empty or blank");
        }

        if (authenticator.authenticate(jwt)) {
            try {
                benchmarkManager.deleteGroup(groupId);
            } catch (NotFoundException e) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().build();
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
