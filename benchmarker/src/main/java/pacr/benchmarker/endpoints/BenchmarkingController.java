package pacr.benchmarker.endpoints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import pacr.benchmarker.services.SystemEnvironment;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Creates a connection between Benchmarker and Web-App.
 *
 * @author Pavel Zwerschke
 */
@Controller
public class BenchmarkingController {

    private static final Logger LOGGER = LogManager.getLogger(BenchmarkingController.class);

    private NewJobHandler newJobHandler;
    private SSHKeyHandler sshKeyHandler;
    private RegisteredHandler registeredHandler;
    private UnregisteredHandler unregisteredHandler;
    private String ipWebApp;

    /**
     * Initializes an instance of BenchmarkingController.
     * @param newJobHandler is the handler for new jobs.
     * @param sshKeyHandler is the handler for updating the ssh key.
     * @param registeredHandler gets called when /register is called.
     * @param unregisteredHandler gets called when /unregister is called.
     * @param ipWebApp is the ip address with port of the Web-App.
     */
    public BenchmarkingController(@NotNull NewJobHandler newJobHandler, @NotNull SSHKeyHandler sshKeyHandler,
                                  @NotNull RegisteredHandler registeredHandler,
                                  @NotNull UnregisteredHandler unregisteredHandler,
                                  @NotNull @Value("${ipWebApp}") String ipWebApp) {
        Objects.requireNonNull(newJobHandler);
        Objects.requireNonNull(sshKeyHandler);
        Objects.requireNonNull(registeredHandler);
        Objects.requireNonNull(unregisteredHandler);
        Objects.requireNonNull(ipWebApp);

        this.newJobHandler = newJobHandler;
        this.sshKeyHandler = sshKeyHandler;
        this.registeredHandler = registeredHandler;
        this.unregisteredHandler = unregisteredHandler;
        this.ipWebApp = ipWebApp;

        openConnection();
    }

    /**
     * Opens a connection between Benchmarker and Web-App.
     */
    public void openConnection() {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session;

        String url = "ws://" + ipWebApp + "/connect";
        try {
            session = stompClient.connect(url, new BenchmarkerSessionHandler()).get(1, TimeUnit.SECONDS);

            newJobHandler.setSession(session);

            session.subscribe("/user/queue/registered", registeredHandler);
            session.subscribe("/user/queue/unregistered", unregisteredHandler);
            session.subscribe("/user/queue/newJob", newJobHandler);
            session.subscribe("/topic/sshKey", sshKeyHandler);

            session.send("/app/register", SystemEnvironment.getInstance());
        } catch (Exception ex) {
            LOGGER.error("Could not connect with PACR-Web-App.");
        }
    }

}
