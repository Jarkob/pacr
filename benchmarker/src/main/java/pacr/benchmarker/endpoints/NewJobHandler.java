package pacr.benchmarker.endpoints;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Component;
import pacr.benchmarker.services.Benchmark;
import pacr.benchmarker.services.Benchmarker;
import pacr.benchmarker.services.IJobResultSender;
import pacr.benchmarker.services.JobResult;

import java.lang.reflect.Type;

/**
 * Handles new jobs and dispatches them.
 *
 * @author Pavel Zwerschke
 */
@Component
public class NewJobHandler implements StompFrameHandler, IJobResultSender {

    private StompSession session;
    private Benchmarker benchmarker;

    /**
     * Creates an instance of NewJobHandler.
     * @param benchmarker will execute jobs.
     */
    public NewJobHandler(Benchmarker benchmarker) {
        this.benchmarker = benchmarker;
    }

    /**
     * Sets the StompSession.
     * @param session is the session being set.
     */
    public void setSession(StompSession session) {
        this.session = session;
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return JobMessage.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        JobMessage job = (JobMessage)o;

        benchmarker.executeJob(job.getRepository(), job.getCommitHash());
    }

    @Override
    public void sendJobResults(JobResult result) {
        session.send("/app/receiveResults", result);
    }

}
