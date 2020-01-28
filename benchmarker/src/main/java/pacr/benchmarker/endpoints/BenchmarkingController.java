package pacr.benchmarker.endpoints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Pavel Zwerschke
 */
@Controller
public class BenchmarkingController {

    private static final Logger LOGGER = LogManager.getLogger(BenchmarkingController.class);

    private NewJobHandler newJobHandler;

    public BenchmarkingController(NewJobHandler newJobHandler) {
        this.newJobHandler = newJobHandler;

        openConnection();
    }

    public void openConnection() {
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        transports.add(new RestTemplateXhrTransport());

        SockJsClient sockJsClient = new SockJsClient(transports);
        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session;

        String url = "ws://127.0.0.1:8080/connect";
        try {
            session = stompClient.connect(url, new BenchmarkerSessionHandler()).get(1, TimeUnit.SECONDS);

            newJobHandler.setSession(session);

            session.subscribe("/user/queue/registered", new RegisteredHandler());
            session.subscribe("/user/queue/unregistered", new UnregisteredHandler());
            session.subscribe("/user/queue/newJob", newJobHandler);
            session.subscribe("/topic/sshKey", new SSHKeyHandler());

            session.send("/app/register", SystemEnvironment.getInstance());
        } catch (Exception ex) {
            LOGGER.error("Could not connect with PACR-Web-App.");
        }
    }

}
