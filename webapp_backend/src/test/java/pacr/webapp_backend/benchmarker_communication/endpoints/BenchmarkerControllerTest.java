package pacr.webapp_backend.benchmarker_communication.endpoints;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import pacr.webapp_backend.benchmarker_communication.services.IBenchmarkerHandler;
import pacr.webapp_backend.benchmarker_communication.services.SystemEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BenchmarkerControllerTest {

    private static final String ADDRESS = "benchmarkerAddress";

    private BenchmarkerController benchmarkerController;

    @Mock
    private IBenchmarkerHandler benchmarkerHandler;

    @Mock
    private SimpMessagingTemplate template;

    @Mock
    private Principal principal;

    @Mock
    private SystemEnvironment systemEnvironment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(principal.getName()).thenReturn(ADDRESS);

        when(benchmarkerHandler.registerBenchmarker(any(), any())).thenReturn(true);
        when(benchmarkerHandler.unregisterBenchmarker(any())).thenReturn(true);

        benchmarkerController = new BenchmarkerController(benchmarkerHandler, template);
    }

    @Test
    void registerBenchmarker_noError() {
        boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, principal);

        verify(benchmarkerHandler).registerBenchmarker(ADDRESS, systemEnvironment);
        assertTrue(result);
    }

    @Test
    void registerBenchmarker_registrationFailed() {
        when(benchmarkerHandler.registerBenchmarker(any(), any())).thenReturn(false);

        boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, principal);

        verify(benchmarkerHandler).registerBenchmarker(ADDRESS, systemEnvironment);
        assertFalse(result);
    }

    @Test
    void registerBenchmarker_nullSystemEnvironment() {
        boolean result = benchmarkerController.registerBenchmarker(null, principal);

        verify(benchmarkerHandler, never()).registerBenchmarker(any(), any());
        assertFalse(result);
    }

    @Test
    void registerBenchmarker_nullPrincipal() {
        boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, null);

        verify(benchmarkerHandler, never()).registerBenchmarker(any(), any());
        assertFalse(result);
    }

    @Test
    void registerBenchmarker_nullAddress() {
        when(principal.getName()).thenReturn(null);

        boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, principal);

        verify(benchmarkerHandler, never()).registerBenchmarker(any(), any());
        assertFalse(result);
    }

    @Test
    void registerBenchmarker_emptyAddress() {
        when(principal.getName()).thenReturn("");

        boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, principal);

        verify(benchmarkerHandler, never()).registerBenchmarker(any(), any());
        assertFalse(result);
    }

    @Test
    void registerBenchmarker_blankAddress() {
        when(principal.getName()).thenReturn(" ");

        boolean result = benchmarkerController.registerBenchmarker(systemEnvironment, principal);

        verify(benchmarkerHandler, never()).registerBenchmarker(any(), any());
        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_noError() {
        boolean result = benchmarkerController.unregisterBenchmarker(principal);

        verify(benchmarkerHandler).unregisterBenchmarker(ADDRESS);
        assertTrue(result);
    }

    @Test
    void unregisterBenchmarker_unregisterFailed() {
        when(benchmarkerHandler.unregisterBenchmarker(any())).thenReturn(false);

        boolean result = benchmarkerController.unregisterBenchmarker(principal);

        verify(benchmarkerHandler).unregisterBenchmarker(ADDRESS);
        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_nullPrincipal() {
        boolean result = benchmarkerController.unregisterBenchmarker(null);

        verify(benchmarkerHandler, never()).unregisterBenchmarker(any());
        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_nullAddress() {
        when(principal.getName()).thenReturn(null);

        boolean result = benchmarkerController.unregisterBenchmarker(principal);

        verify(benchmarkerHandler, never()).unregisterBenchmarker(any());
        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_emptyAddress() {
        when(principal.getName()).thenReturn("");

        boolean result = benchmarkerController.unregisterBenchmarker(principal);

        verify(benchmarkerHandler, never()).unregisterBenchmarker(any());
        assertFalse(result);
    }

    @Test
    void unregisterBenchmarker_blankAddress() {
        when(principal.getName()).thenReturn(" ");

        boolean result = benchmarkerController.unregisterBenchmarker(principal);

        verify(benchmarkerHandler, never()).unregisterBenchmarker(any());
        assertFalse(result);
    }

    @Test
    void getBenchmarkerSystemEnvironments_noError() {
        Collection<SystemEnvironment> expectedEnvironmentsCollection = mock(Collection.class);
        when(benchmarkerHandler.getBenchmarkerSystemEnvironment()).thenReturn(expectedEnvironmentsCollection);

        Collection<SystemEnvironment> environments = benchmarkerController.getBenchmarkerSystemEnvironments();

        verify(benchmarkerHandler).getBenchmarkerSystemEnvironment();
        assertEquals(expectedEnvironmentsCollection, environments);
    }

    @Test
    void getBenchmarkerSystemEnvironments_nullEnvironments() {
        when(benchmarkerHandler.getBenchmarkerSystemEnvironment()).thenReturn(null);

        Collection<SystemEnvironment> environments = benchmarkerController.getBenchmarkerSystemEnvironments();

        verify(benchmarkerHandler).getBenchmarkerSystemEnvironment();
        assertNotNull(environments);
    }

    @Test
    void sendSSHKey_noError() {
        final String sshKey = "sshKey";
        final String expectedPath = "/topic/sshKey";

        benchmarkerController.sendSSHKey(sshKey);

        ArgumentCaptor<SSHKeyMessage> captorMessage = ArgumentCaptor.forClass(SSHKeyMessage.class);
        ArgumentCaptor<String> captorPath = ArgumentCaptor.forClass(String.class);

        verify(template).convertAndSend(captorPath.capture(), captorMessage.capture());

        String path = captorPath.getValue();
        assertEquals(expectedPath, path);

        SSHKeyMessage message = captorMessage.getValue();
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

}
