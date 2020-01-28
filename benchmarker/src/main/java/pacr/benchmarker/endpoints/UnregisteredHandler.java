package pacr.benchmarker.endpoints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;

/**
 * @author Pavel Zwerschke
 */
public class UnregisteredHandler implements StompFrameHandler {

    private Logger logger = LogManager.getLogger(BenchmarkerSessionHandler.class);

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return boolean.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        boolean message = (boolean) payload;

        if (message) {
            logger.info("Unregistration was successful.");
        } else {
            logger.error("Unregistration failed.");
        }
    }

}
