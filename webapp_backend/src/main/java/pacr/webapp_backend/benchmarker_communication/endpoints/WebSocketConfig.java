package pacr.webapp_backend.benchmarker_communication.endpoints;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configures Websockets for this Spring application.
 */
@Configuration
@EnableScheduling
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Setup the message broker to be memory based.
     * /topic is used for a broadcast to all registered clients
     * /queue is used to send messages to a specific client
     * Websocket requests must start with /app.
     *
     * @param config the message broker configuration.
     */
    @Override
    public void configureMessageBroker(final MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic/", "/queue/");
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Set /connect as the entry point for clients where they are assigned a unique name.
     *
     * @param registry the endpoint registry.
     */
    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        registry.addEndpoint("/connect").setHandshakeHandler(new AssignPrincipalHandshakeHandler()).withSockJS();
    }

}
