package pacr.webapp_backend.benchmarker_communication.endpoints;

import java.security.Principal;
import java.util.Map;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

/**
 * This class assigns a name to each new client that is registered.
 * If the client is already registered it gets its old name reassigned.
 */
class AssignPrincipalHandshakeHandler extends DefaultHandshakeHandler {

    private static final String ATTR_PRINCIPAL = "__principal__";

    private static final String CLIENT_PREFIX = "client";
    private static int clientID;

    /**
     * Checks whether the client already has a name associated and assigns it to the client.
     * If the client is new, a new unique name is created and assigned.
     *
     * @param request the HTTP request.
     * @param wsHandler the web socket handler.
     * @param attributes all attributes that are set for the client.
     *
     * @return a principal object that carries the name of the client.
     */
    @Override
    protected Principal determineUser(final ServerHttpRequest request, final WebSocketHandler wsHandler,
                                      final Map<String, Object> attributes) {
        final String name;

        if (attributes.containsKey(ATTR_PRINCIPAL)) {
            name = (String) attributes.get(ATTR_PRINCIPAL);
        } else {
            name = generateUsername();
            attributes.put(ATTR_PRINCIPAL, name);
        }

        return () -> name;
    }

    /**
     * Creates a new unique name based on a prefix and an id that is incremented every
     * time a new name is created.
     *
     * @return a unique name.
     */
    private static synchronized String generateUsername() {
        return CLIENT_PREFIX + clientID++;
    }
}
