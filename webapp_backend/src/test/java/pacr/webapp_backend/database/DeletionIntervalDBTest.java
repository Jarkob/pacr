package pacr.webapp_backend.database;

import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class DeletionIntervalDBTest {

    DeletionIntervalDB delIntervalDB;

    @Autowired
    public DeletionIntervalDBTest(DeletionIntervalDB delIntervalDB) {
        MockitoAnnotations.initMocks(this);

        this.delIntervalDB = delIntervalDB;
    }


    @Test
    void getDeletionInterval_NoDeletionIntervalSet_ShouldThrowException() {
        assertThrows(NoSuchElementException.class, () -> delIntervalDB.getDeletionInterval());
    }

    @Test
    void setDeletionInterval_NormalValues_ShouldReturnSavedDeletionInterval() {
        delIntervalDB.setDeletionInterval(10);
        assertEquals(10, delIntervalDB.getDeletionInterval());
        delIntervalDB.setDeletionInterval(40);
        assertEquals(40, delIntervalDB.getDeletionInterval());
    }


}
