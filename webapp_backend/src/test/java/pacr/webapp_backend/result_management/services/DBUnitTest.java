package pacr.webapp_backend.result_management.services;

import java.util.Collection;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import pacr.webapp_backend.SpringBootTestWithoutShell;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
public class DBUnitTest extends SpringBootTestWithoutShell {

    @Autowired
    private BenchmarkManager benchmarkManager;

    @Test
    @DatabaseSetup(value = "/group-four-data.xml")
    @DatabaseTearDown
    public void getAllGroups_fourSaved_shouldReturnFour() throws InterruptedException {
        Collection<BenchmarkGroup> groups = benchmarkManager.getAllGroups();
        assertEquals(4, groups.size());
    }

    @Test
    @DatabaseSetup(value = "/group-three-data.xml")
    @DatabaseTearDown
    public void getAllGroups_threeSaved_shouldReturnThree() throws InterruptedException {
        Collection<BenchmarkGroup> groups = benchmarkManager.getAllGroups();
        assertEquals(3, groups.size());
    }

}