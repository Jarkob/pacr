package pacr.webapp_backend.result_management.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pacr.webapp_backend.result_management.endpoints.BenchmarkController;
import pacr.webapp_backend.result_management.endpoints.BenchmarkInput;
import pacr.webapp_backend.shared.IAuthenticator;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class BenchmarkControllerTest {

    public static final String TOKEN = "token";
    public static final String BENCHMARK_NAME = "benchmark";
    public static final String GROUP_NAME = "group";
    public static final String BENCHMARK_DESC = "description";
    public static final int BENCHMARK_ID = 1;
    public static final int GROUP_ID = 1;

    @Mock
    private BenchmarkManager benchmarkManagerMock;
    @Mock
    private IAuthenticator authenticatorMock;

    private BenchmarkInput benchmarkInput;

    private BenchmarkController benchmarkController;

    private MockMvc mockMvc;
    
    @BeforeEach
    public void setUp() {
        benchmarkManagerMock = Mockito.mock(BenchmarkManager.class);
        authenticatorMock = Mockito.mock(IAuthenticator.class);

        benchmarkController = new BenchmarkController(authenticatorMock, benchmarkManagerMock);

        benchmarkInput = new BenchmarkInput(BENCHMARK_ID, BENCHMARK_NAME, BENCHMARK_DESC, GROUP_ID);

        mockMvc = standaloneSetup(benchmarkController).build();
    }

    /**
     * Tests whether getAllBenchmarks returns the same as the benchmark manager.
     */
    @Test
    void getAllBenchmarks_shouldReturnSameAsBenchmarkManager() {
        List<Benchmark> benchmarks = new LinkedList<>();
        when(benchmarkManagerMock.getAllBenchmarks()).thenReturn(benchmarks);

        Collection<Benchmark> testBenchmarks = benchmarkController.getAllBenchmarks();

        assertEquals(benchmarks, testBenchmarks);
    }

    /**
     * Tests whether getAllBenchmarks returns the same as the benchmark manager.
     */
    @Test
    void getAllBenchmarks_apiCall_shouldReturnSameAsBenchmarkManager() throws Exception {
        List<Benchmark> benchmarks = new LinkedList<>();
        Benchmark benchmark = new Benchmark(BENCHMARK_NAME);
        benchmarks.add(benchmark);

        when(benchmarkManagerMock.getAllBenchmarks()).thenReturn(benchmarks);

        mockMvc.perform(MockMvcRequestBuilders.get("/benchmarks"))
        .andExpect(content().string("[{\"id\":0,\"originalName\":\"benchmark\",\"customName\":\"benchmark\",\"description\":\"\",\"properties\":[],\"group\":null}]"));
    }

    /**
     * Tests whether getAllGroups returns the same as the benchmark manager.
     */
    @Test
    void getAllGroups_shouldReturnSameAsBenchmarkManager() {
        List<BenchmarkGroup> groups = new LinkedList<>();
        when(benchmarkManagerMock.getAllGroups()).thenReturn(groups);

        Collection<BenchmarkGroup> testGroups = benchmarkController.getAllGroups();

        assertEquals(groups, testGroups);
    }

    /**
     * Tests whether updateBenchmark updates it on the benchmark manager and if it authenticates the token correctly.
     */
    @Test
    void updateBenchmark_authenticationSucceeds_shouldCallUpdateBenchmarkInManager() {
        when(authenticatorMock.authenticate(TOKEN)).thenReturn(true);

        ResponseEntity<Object> response = benchmarkController.updateBenchmark(benchmarkInput, TOKEN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticatorMock).authenticate(TOKEN);
        verify(benchmarkManagerMock).updateBenchmark(BENCHMARK_ID, BENCHMARK_NAME, BENCHMARK_DESC, GROUP_ID);
    }

    /**
     * Tests whether updateBenchmark doesn't update it on the benchmark manager if authentication fails.
     */
    @Test
    void updateBenchmark_authenticationFails_shouldNotUpdateBenchmark() {
        when(authenticatorMock.authenticate(TOKEN)).thenReturn(false);

        ResponseEntity<Object> response = benchmarkController.updateBenchmark(benchmarkInput, TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authenticatorMock).authenticate(TOKEN);
        verify(benchmarkManagerMock, never()).updateBenchmark(anyInt(), anyString(), anyString(), anyInt());
    }

    /**
     * Tests whether addGroup adds it on the benchmark manager and if it authenticates the token correctly.
     */
    @Test
    void addGroup_authenticationSucceeds_shouldCallAddGroupInManager() {
        when(authenticatorMock.authenticate(TOKEN)).thenReturn(true);

        ResponseEntity<Object> response = benchmarkController.addGroup(GROUP_NAME, TOKEN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticatorMock).authenticate(TOKEN);
        verify(benchmarkManagerMock).addGroup(GROUP_NAME);
    }

    /**
     * Tests whether addGroup doesn't add it on the benchmark manager if authentication fails.
     */
    @Test
    void addGroup_authenticationFails_shouldNotAddGroup() {
        when(authenticatorMock.authenticate(TOKEN)).thenReturn(false);

        ResponseEntity<Object> response = benchmarkController.addGroup(GROUP_NAME, TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authenticatorMock).authenticate(TOKEN);
        verify(benchmarkManagerMock, never()).addGroup(anyString());
    }

    /**
     * Tests whether updateGroup updates it on the benchmark manager and if it authenticates the token correctly.
     */
    @Test
    void updateGroup_authenticationSucceeds_shouldCallUpdateGroupInManager() {
        when(authenticatorMock.authenticate(TOKEN)).thenReturn(true);

        ResponseEntity<Object> response = benchmarkController.updateGroup(GROUP_ID, GROUP_NAME, TOKEN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticatorMock).authenticate(TOKEN);
        verify(benchmarkManagerMock).updateGroup(GROUP_ID, GROUP_NAME);
    }

    /**
     * Tests whether updateGroup doesn't update it on the benchmark manager if authentication fails.
     */
    @Test
    void updateGroup_authenticationFails_shouldNotUpdateGroup() {
        when(authenticatorMock.authenticate(TOKEN)).thenReturn(false);

        ResponseEntity<Object> response = benchmarkController.updateGroup(GROUP_ID, GROUP_NAME, TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authenticatorMock).authenticate(TOKEN);
        verify(benchmarkManagerMock, never()).updateGroup(anyInt(), anyString());
    }

    /**
     * Tests whether deleteGroup deletes it on the benchmark manager and if it authenticates the token correctly.
     */
    @Test
    void deleteGroup_authenticationSucceeds_shouldCallDeleteGroupInManager() throws IllegalAccessException {
        when(authenticatorMock.authenticate(TOKEN)).thenReturn(true);

        ResponseEntity<Object> response = benchmarkController.deleteGroup(GROUP_ID, TOKEN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(authenticatorMock).authenticate(TOKEN);
        verify(benchmarkManagerMock).deleteGroup(GROUP_ID);
    }

    /**
     * Tests whether deleteGroup doesn't delete it on the benchmark manager if authentication fails.
     */
    @Test
    void deleteGroup_authenticationFails_shouldNotDeleteGroup() throws IllegalAccessException {
        when(authenticatorMock.authenticate(TOKEN)).thenReturn(false);

        ResponseEntity<Object> response = benchmarkController.deleteGroup(GROUP_ID, TOKEN);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verify(authenticatorMock).authenticate(TOKEN);
        verify(benchmarkManagerMock, never()).deleteGroup(anyInt());
    }

    /**
     * Tests whether getBenchmarksByGroup properly forwards the answer from the benchmark manager.
     */
    @Test
    void getBenchmarksByGroup_shouldCallMethodInManager() {
        List<Benchmark> benchmarks = new LinkedList<>();
        when(benchmarkManagerMock.getBenchmarksByGroup(GROUP_ID)).thenReturn(benchmarks);

        Collection<Benchmark> testBenchmarks = benchmarkController.getBenchmarksByGroup(GROUP_ID);

        assertEquals(benchmarks, testBenchmarks);
    }
}
