package pacr.webapp_backend.database;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for PullIntervalDB.
 *
 * @author Pavel Zwerschke
 */
@SpringBootTest
public class PullIntervalDBTest {

    private PullIntervalDB pullIntervalDB;

    @Autowired
    public PullIntervalDBTest(PullIntervalDB pullIntervalDB) {
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
