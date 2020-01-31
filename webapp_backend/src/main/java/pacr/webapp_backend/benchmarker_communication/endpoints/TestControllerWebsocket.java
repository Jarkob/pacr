package pacr.webapp_backend.benchmarker_communication.endpoints;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pacr.webapp_backend.shared.IJobScheduler;

import java.util.Arrays;
import java.util.List;

/**
 * @author Pavel Zwerschke
 */
@RestController
public class TestControllerWebsocket {

    private static final Logger LOGGER = LogManager.getLogger(TestControllerWebsocket.class);
    private IJobScheduler jobScheduler;

    /*
    private static final List<String> commitsLEAN = Arrays.asList(
            "ac3e13cab924064dcfaddbba8f56fddac0bde1e7",
            "9a85e6074e8e77fa51a6712eb64e269bf9efb6a0",
            "43677f89fb61aafae1fbc2c65ed9a7f6895c005a",
            "ffb19266f6859fbab85bc2486794fa228bd8c7a1",
            "1460e5a5b368488cbc62e1eb1a21f3497466671f",
            "b4bb80db7c69dc81d03fb7633bb0acafb1c1d19f",
            "252c27e52fe25c5371e16ccedd235c6cd474c6ca",
            "0f02d8f6506ef78e660a71a19571422aaeb70f3b");

     */

    private static final List<String> commitsLEAN = Arrays.asList(
            "fff2899176869d9094b3314a48937d953aa1437a",
            "e50b2040b636771103a69f484b95c94bc4a11d46",
            "90430e96968d49d64e3d5c22cb0180ab943498a6",
            "ccf400506ab29b9b37afa24450de2b285cc5ec75",
            "356a4fafcdd3b5248068657c948ed90b79476f3d",
            "d3097d08c1691581da8e72b01d689750687d51fe"
    );

    private static final List<String> commitsTest = Arrays.asList(
            "39e1a8c8f9951015a101c18c55533947d0a44bdd",
            "9c8c86f5939c88329d9f46f7f5266f6c6b2e515e",
            "e68151d6e1031609238c0a12ecbea8ce478b0c70",
            "e4e234247dbb8f18c77c9c8678788735e15b7fcb",
            "08af11060c72caa7168bbf5fb4f59cc432dcbc96",
            "ab1008e07bfe9c84b9cc994eb02e5e5bb241a98f",
            "6fde0d353d758700e03f95885c079b2cfafbf00f",
            "ac782173736902511a6f3214e0cac3068b27a448"
    );

    int commitHashNR = 0;

    public TestControllerWebsocket(IJobScheduler jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

}
