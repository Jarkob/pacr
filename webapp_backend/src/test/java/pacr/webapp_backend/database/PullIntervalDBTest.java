package pacr.webapp_backend.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pacr.webapp_backend.SpringBootTestWithoutShell;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for PullIntervalDB.
 *
 * @author Pavel Zwerschke
 */
public class PullIntervalDBTest extends SpringBootTestWithoutShell {

    private final PullIntervalDB pullIntervalDB;

    @Autowired
    public PullIntervalDBTest(final PullIntervalDB pullIntervalDB) {
        this.pullIntervalDB = pullIntervalDB;
    }

    /**
     * Checks the getter and setter method.
     */
    @Test
    public void setterGetter() {
        pullIntervalDB.setPullInterval(5);
        assertEquals(5, pullIntervalDB.getPullInterval());
    }

}
