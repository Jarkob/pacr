package pacr.webapp_backend.benchmarker_communication.endpoints;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pacr.webapp_backend.benchmarker_communication.services.IBenchmarkerHandler;
import pacr.webapp_backend.benchmarker_communication.services.IJobRegistry;
import pacr.webapp_backend.benchmarker_communication.services.SystemEnvironment;
import pacr.webapp_backend.shared.IJob;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BenchmarkerControllerTest {

    private static final String ADDRESS = "benchmarkerAddress";

    private BenchmarkerController benchmarkerController;

    @Mock
    private IBenchmarkerHandler benchmarkerHandler;

    @Mock
    private IJobRegistry jobRegistry;

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private Principal principal;

    @Mock
    private SystemEnvironment systemEnvironment;

    @Mock
    private IJob benchmarkerJob;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(principal.getName()).thenReturn(ADDRESS);

        when(benchmarkerHandler.registerBenchmarker(any(), any())).thenReturn(true);
        when(benchmarkerHandler.unregisterBenchmarker(any())).thenReturn(true);
        when(benchmarkerHandler.getBenchmarkerSystemEnvironment(ADDRESS)).thenReturn(systemEnvironment);

        when(jobRegistry.getCurrentBenchmarkerJob(ADDRESS)).thenReturn(benchmarkerJob);

        benchmarkerController = new BenchmarkerController(benchmarkerHandler, jobRegistry, template);
    }

    @Test
    void registerBenchmarker_noError() {
        final boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, principal);

        verify(benchmarkerHandler).registerBenchmarker(ADDRESS, systemEnvironment);
        assertTrue(result);
    }

    @Test
    void registerBenchmarker_registrationFailed() {
        when(benchmarkerHandler.registerBenchmarker(any(), any())).thenReturn(false);

        final boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, principal);

        verify(benchmarkerHandler).registerBenchmarker(ADDRESS, systemEnvironment);
        assertFalse(result);
    }

    @Test
    void registerBenchmarker_nullSystemEnvironment() {
        final boolean result = benchmarkerController.registerBenchmarker(null, principal);

        verify(benchmarkerHandler, never()).registerBenchmarker(any(), any());
        assertFalse(result);
    }

    @Test
    void registerBenchmarker_nullPrincipal() {
        final boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, null);

        verify(benchmarkerHandler, never()).registerBenchmarker(any(), any());
        assertFalse(result);
    }

    @Test
    void registerBenchmarker_nullAddress() {
        when(principal.getName()).thenReturn(null);

        final boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, principal);

        verify(benchmarkerHandler, never()).registerBenchmarker(any(), any());
        assertFalse(result);
    }

    @Test
    void registerBenchmarker_emptyAddress() {
        when(principal.getName()).thenReturn("");

        final boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, principal);

        verify(benchmarkerHandler, never()).registerBenchmarker(any(), any());
        assertFalse(result);
    }

    @Test
    void registerBenchmarker_blankAddress() {
        when(principal.getName()).thenReturn(" ");

        final boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, principal);

        verify(benchmarkerHandler, never()).registerBenchmarker(any(), any());
        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_noError() {
        final boolean result = benchmarkerController.unregisterBenchmarker(principal);

        verify(benchmarkerHandler).unregisterBenchmarker(ADDRESS);
        assertTrue(result);
    }

    @Test
    void unregisterBenchmarker_unregisterFailed() {
        when(benchmarkerHandler.unregisterBenchmarker(any())).thenReturn(false);

        final boolean result = benchmarkerController.unregisterBenchmarker(principal);

        verify(benchmarkerHandler).unregisterBenchmarker(ADDRESS);
        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_nullPrincipal() {
        final boolean result = benchmarkerController.unregisterBenchmarker(null);

        verify(benchmarkerHandler, never()).unregisterBenchmarker(any());
        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_nullAddress() {
        when(principal.getName()).thenReturn(null);

        final boolean result = benchmarkerController.unregisterBenchmarker(principal);

        verify(benchmarkerHandler, never()).unregisterBenchmarker(any());
        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_emptyAddress() {
        when(principal.getName()).thenReturn("");

        final boolean result = benchmarkerController.unregisterBenchmarker(principal);

        verify(benchmarkerHandler, never()).unregisterBenchmarker(any());
        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_blankAddress() {
        when(principal.getName()).thenReturn(" ");

        final boolean result = benchmarkerController.unregisterBenchmarker(principal);

        verify(benchmarkerHandler, never()).unregisterBenchmarker(any());
        assertFalse(result);
    }

    @Test
    void SSHKeyMessage_noArgs() {
        assertDoesNotThrow(() -> {
            SSHKeyMessage sshKeyMessage = new SSHKeyMessage();
        });
    }

    @Test
    void sendSSHKey_noError() {
        final String sshKey = "sshKey";
        final String expectedPath = "/topic/sshKey";

        benchmarkerController.sendSSHKey(sshKey);

        final ArgumentCaptor<SSHKeyMessage> captorMessage = ArgumentCaptor.forClass(SSHKeyMessage.class);
        final ArgumentCaptor<String> captorPath = ArgumentCaptor.forClass(String.class);

        verify(template).convertAndSend(captorPath.capture(), captorMessage.capture());

        final String path = captorPath.getValue();
        assertEquals(expectedPath, path);

        final SSHKeyMessage message = captorMessage.getValue();
        assertNotNull(message);
        assertEquals(message.getSshKey(), sshKey);
    }

    @Test
    void sendSSHKey_nullSSHKey() {
        benchmarkerController.sendSSHKey(null);

        verify(template, never()).convertAndSend(any(Object.class), any());
    }

    @Test
    void sendSSHKey_emptySSHKey() {
        benchmarkerController.sendSSHKey("");

        verify(template, never()).convertAndSend(any(Object.class), any());
    }

    @Test
    void sendSSHKey_blankSSHKey() {
        benchmarkerController.sendSSHKey(" ");

        verify(template, never()).convertAndSend(any(Object.class), any());
    }

    @Test
    void getBenchmarkers_noError() {
        when(benchmarkerHandler.getAllBenchmarkerAddresses()).thenReturn(List.of(ADDRESS));

        final Collection<Benchmarker> benchmarkers = benchmarkerController.getBenchmarkers();

        assertNotNull(benchmarkers);
        assertEquals(1, benchmarkers.size());

        final Benchmarker benchmarker = benchmarkers.stream().findFirst().orElse(null);
        assertNotNull(benchmarker);

        assertEquals(systemEnvironment, benchmarker.getSystemEnvironment());
        assertEquals(benchmarkerJob, benchmarker.getCurrentJob());
        assertEquals(ADDRESS, benchmarker.getAddress());
    }
}
