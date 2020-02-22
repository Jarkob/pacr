package pacr.webapp_backend.benchmarker_communication.endpoints;

import java.security.Principal;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import pacr.webapp_backend.benchmarker_communication.services.BenchmarkerJob;
import pacr.webapp_backend.benchmarker_communication.services.IJobSender;
import pacr.webapp_backend.benchmarker_communication.services.JobHandler;
import pacr.webapp_backend.benchmarker_communication.services.JobResult;

/**
 * Sends out jobs to PACR-Benchmarkers and receives their results.
 */
@RestController
public class BenchmarkingJobsController implements IJobSender, ApplicationListener<SessionDisconnectEvent> {

    private JobHandler jobHandler;

    private SimpMessagingTemplate template;

    /**
     * Creates a new BenchmarkingJobsController.
     *
     * @param template a messaging template to send messages to clients.
     */
    public BenchmarkingJobsController(SimpMessagingTemplate template) {
        this.template = template;
    }

    /**
     * Setter injection for the jobHandler so a circular dependency injection is avoided.
     *
     * @param jobHandler the used jobHandler.
     */
    @Autowired
    public void setJobHandler(@NotNull JobHandler jobHandler) {
        Objects.requireNonNull(jobHandler, "The jobHandler cannot be null.");

        this.jobHandler = jobHandler;
    }

    /**
     * Receives results from a benchmarker and delegates them to the jobHandler.
     *
     * @param result the result from the benchmarker.
     * @param principal the principal assigned by the handshake handler.
     *
     * @return if the results were received correctly.
     */
    @MessageMapping("/receiveResults")
    public boolean receiveBenchmarkingResults(JobResult result, Principal principal) {
        if (principal == null || result == null) {
            return false;
        }

        final String address = principal.getName();

        if (stringIsValid(address)) {
            jobHandler.receiveBenchmarkingResults(address, result);
            return true;
        }

        return false;
    }

    @Override
    public boolean sendJob(BenchmarkerJob benchmarkerJob) {
        if (!benchmarkerJobIsValid(benchmarkerJob)) {
            return false;
        }

        JobMessage jobMessage = new JobMessage(benchmarkerJob.getRepository(), benchmarkerJob.getCommitHash());

        template.convertAndSendToUser(benchmarkerJob.getAddress(), "/queue/newJob", jobMessage);

        return true;
    }

    private boolean benchmarkerJobIsValid(BenchmarkerJob benchmarkerJob) {
        return benchmarkerJob != null
                && stringIsValid(benchmarkerJob.getAddress())
                && stringIsValid(benchmarkerJob.getRepository())
                && stringIsValid(benchmarkerJob.getCommitHash());
    }

    private boolean stringIsValid(String str) {
        return str != null && !str.isEmpty() && !str.isBlank();
    }

    /**
     * Gets called when the app detects that a client was disconnected and delegates it to the
     * jobHandler.
     *
     * @param sessionDisconnectEvent the event that is created when a client disconnects.
     */
    @Override
    public void onApplicationEvent(SessionDisconnectEvent sessionDisconnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());

        if (headerAccessor.getSessionAttributes() != null) {
            final String address = (String) headerAccessor.getSessionAttributes().get("__principal__");

            if (stringIsValid(address)) {
                jobHandler.connectionLostFor(address);
            }
        }
    }
}
