package pacr.webapp_backend.dashboard_management.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import pacr.webapp_backend.SpringBootTestWithoutShell;
import pacr.webapp_backend.dashboard_management.services.DashboardManager;
import pacr.webapp_backend.database.DeletionIntervalDB;
import pacr.webapp_backend.shared.IAuthenticator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeletionIntervalControllerTest extends SpringBootTestWithoutShell {

    DeletionIntervalController deletionIntervalController;

    DeletionIntervalDB deletionIntervalDB;

    @Mock
    private IAuthenticator authenticator;

    private DashboardManager dashboardManager;

    @Autowired
    DeletionIntervalControllerTest(DashboardManager dashboardManager, DeletionIntervalDB deletionIntervalDB) {
        this.dashboardManager = dashboardManager;
        this.deletionIntervalDB = deletionIntervalDB;
    }

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);

        this.deletionIntervalController = new DeletionIntervalController(dashboardManager, authenticator);
        deletionIntervalDB.delete();
    }

    @Test
    void changeDeletionInterval_InvalidValue_ShouldReturnBadRequest() {
        final String token = "jwt";

        when(authenticator.authenticate(token)).thenReturn(true);
        assertEquals(HttpStatus.BAD_REQUEST, deletionIntervalController.
                changeDeletionInterval(0, token).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, deletionIntervalController.
                changeDeletionInterval(-1, token).getStatusCode());
    }

    @Test
    void changeDeletionInterval_InvalidToken_ShouldReturnBadRequest() {
        final String token = "jwt";

        when(authenticator.authenticate(token)).thenReturn(false);
        assertEquals(HttpStatus.BAD_REQUEST, deletionIntervalController.
                changeDeletionInterval(5, token).getStatusCode());
    }

    @Test
    void changeDeletionInterval_ValidValue_ShouldChangeValue() {
        final String token = "jwt";

        when(authenticator.authenticate(token)).thenReturn(true);

        assertEquals(HttpStatus.OK, deletionIntervalController.
                changeDeletionInterval(12, token).getStatusCode());
        verify(authenticator).authenticate(token);


        assertEquals(12, deletionIntervalController.getDeletionInterval().getBody());
        verify(authenticator).authenticate(token);
    }

}
