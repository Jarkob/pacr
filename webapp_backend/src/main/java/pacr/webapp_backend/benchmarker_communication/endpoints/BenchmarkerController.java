package pacr.webapp_backend.benchmarker_communication.endpoints;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import pacr.webapp_backend.benchmarker_communication.services.IBenchmarkerConfigurationSender;
import pacr.webapp_backend.benchmarker_communication.services.IBenchmarkerHandler;
import pacr.webapp_backend.benchmarker_communication.services.IJobRegistry;
import pacr.webapp_backend.benchmarker_communication.services.SystemEnvironment;
import pacr.webapp_backend.shared.IJob;

/**
 * Handles the registration and configuration of PACR-Benchmarkers.
 */
@RestController
public class BenchmarkerController
        implements IBenchmarkerConfigurationSender, ApplicationListener<SessionDisconnectEvent> {

    private final IBenchmarkerHandler benchmarkerHandler;

    private final IJobRegistry jobRegistry;

    private final SimpMessagingTemplate template;

    /**
     * Creates a new BenchmarkerController.
     *
     * @param benchmarkerHandler the benchmarkerHandler used to register benchmarkers.
     * @param jobRegistry provides information about all currently dispatched jobs.
     * @param template a messaging template to send messages to clients.
     */
    public BenchmarkerController(final IBenchmarkerHandler benchmarkerHandler, @Lazy final IJobRegistry jobRegistry, final SimpMessagingTemplate template) {
        this.benchmarkerHandler = benchmarkerHandler;
        this.jobRegistry = jobRegistry;
        this.template = template;
    }

    /**
     * Registers a new benchmarker and saves its current system environment.
     *
     * @param systemEnvironment the current system environment of the benchmarker.
     * @param principal the principal assigned by the handshake handler.
     *
     * @return if the registration was successful.
     */
    @MessageMapping("/register")
    @SendToUser("/queue/registered")
    public boolean registerBenchmarker(final SystemEnvironment systemEnvironment, final Principal principal) {
        if (principal == null || systemEnvironment == null) {
            return false;
        }

        final String address = principal.getName();

        if (stringIsValid(address)) {
            return benchmarkerHandler.registerBenchmarker(address, systemEnvironment);
        }

        return false;
    }

    /**
     * Removes a benchmarker from the system.
     *
     * @param principal the principal assigned by the handshake handler.
     *
     * @return if the benchmarker was successfully removed.
     */
    @MessageMapping("/unregister")
    @SendToUser("/queue/unregistered")
    public boolean unregisterBenchmarker(final Principal principal) {
        if (principal == null) {
            return false;
        }

        final String address = principal.getName();

        if (stringIsValid(address)) {
            return benchmarkerHandler.unregisterBenchmarker(address);
        }

        return false;
    }

    /**
     * @return a list of the system environments of all registered benchmarkers.
     */
    @RequestMapping("/benchmarkers")
    public Collection<Benchmarker> getBenchmarkers() {
        final Collection<String> allBenchmarkerAddresses = benchmarkerHandler.getAllBenchmarkerAddresses();

        final ArrayList<Benchmarker> allBenchmarkers = new ArrayList<>();

        for (final String address : allBenchmarkerAddresses) {
            final SystemEnvironment systemEnvironment = benchmarkerHandler.getBenchmarkerSystemEnvironment(address);
            final IJob currentJob = jobRegistry.getCurrentBenchmarkerJob(address);

            allBenchmarkers.add(new Benchmarker(address, systemEnvironment, currentJob));
        }

        Collections.sort(allBenchmarkers);

        return allBenchmarkers;
    }

    /**
     * Sends the current private ssh key to all registered benchmarkers.
     *
     * @param sshKey the current private ssh key.
     */
    @Override
    public void sendSSHKey(final String sshKey) {
        if (stringIsValid(sshKey)) {
            template.convertAndSend("/topic/sshKey", new SSHKeyMessage(sshKey));
        }
    }

    private boolean stringIsValid(final String str) {
        return str != null && !str.isEmpty() && !str.isBlank();
    }

    /**
     * Gets called when the app detects that a client was disconnected and removes it from the
     * benchmarkerHandler.
     *
     * @param sessionDisconnectEvent the event that is created when a client disconnects.
     */
    @Override
    public void onApplicationEvent(final SessionDisconnectEvent sessionDisconnectEvent) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());

        if (headerAccessor.getSessionAttributes() != null) {
            final String address = (String) headerAccessor.getSessionAttributes().get("__principal__");

            if (stringIsValid(address)) {
                benchmarkerHandler.unregisterBenchmarker(address);
            }
        }
    }
}
