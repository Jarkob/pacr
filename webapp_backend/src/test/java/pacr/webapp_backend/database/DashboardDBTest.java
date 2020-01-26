package pacr.webapp_backend.database;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pacr.webapp_backend.dashboard_management.CommitHistoryDashboardModule;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.LineDiagramDashboardModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DashboardDBTest {

    DashboardDB dashboardDB;

    Dashboard dashboard1;
    Dashboard dashboard2;

    @Autowired
    public DashboardDBTest(DashboardDB dashboardDB) {
        MockitoAnnotations.initMocks(this);

        this.dashboardDB = dashboardDB;
    }

    @BeforeEach
    void init() {
        dashboard1 = new Dashboard("test");
        dashboard1.initializeKeys("editKeyA", "viewKeyA");


        LineDiagramDashboardModule ldm = new LineDiagramDashboardModule();
        ldm.setTrackedBenchmarks(new ArrayList<String>(Arrays.asList("bench1", "benchB")));
        ldm.setTrackedRepositories(new ArrayList<String>(Arrays.asList("repoA", "repo2")));
        dashboard1.addModule(ldm);

        dashboard2 = new Dashboard();
        dashboard2.initializeKeys("editKeyB", "viewKeyB");

        CommitHistoryDashboardModule chdm = new CommitHistoryDashboardModule();
        chdm.setTrackedRepositories(new ArrayList<String>(Arrays.asList("repo 1", "repo B")));

        dashboard2.addModule(chdm);
    }

    @AfterEach
    void setUp() {
        dashboardDB.deleteAll();
    }

    @Test
    void storeDashboard_UsualDashboards_ShouldBeRetrievable() {
        dashboardDB.storeDashboard(dashboard1);
        Dashboard retrieved = dashboardDB.findByEditKey(dashboard1.getEditKey());

        assertTrue(dashboard1.equals(retrieved));

        dashboardDB.storeDashboard(dashboard2);
        retrieved = dashboardDB.findByEditKey(dashboard2.getEditKey());

        assertTrue(dashboard2.equals(retrieved));
    }

    @Test
    void findByEditKey_UsualDashboards_ShouldGetRequestedDashboards() {
        dashboardDB.storeDashboard(dashboard1);
        Dashboard retrieved = dashboardDB.findByEditKey(dashboard1.getEditKey());

        assertTrue(dashboard1.equals(retrieved));

        dashboardDB.storeDashboard(dashboard2);
        retrieved = dashboardDB.findByEditKey(dashboard2.getEditKey());

        assertTrue(dashboard2.equals(retrieved));
    }

    @Test
    void findByViewKey_UsualDashboards_ShouldGetRequestedDashboards() {
        dashboardDB.storeDashboard(dashboard1);
        Dashboard retrieved = dashboardDB.findByViewKey(dashboard1.getViewKey());

        assertTrue(dashboard1.equals(retrieved));

        dashboardDB.storeDashboard(dashboard2);
        retrieved = dashboardDB.findByViewKey(dashboard2.getViewKey());

        assertTrue(dashboard2.equals(retrieved));
    }

    @Test
    void findAll_MultipleDashboards_ShouldReturnAllDashboards() {
        dashboardDB.storeDashboard(dashboard1);
        dashboardDB.storeDashboard(dashboard2);

        Collection<Dashboard> dashboards = dashboardDB.findAll();

        assertTrue(dashboards.contains(dashboard1));
        assertTrue(dashboards.contains(dashboard2));
    }

    @Test
    void findALl_NoDashboards_ShouldReturnEmptyCollection() {
        assertTrue(dashboardDB.findAll().isEmpty());
    }

    @Test
    void delete_NoSuchDashboard_ShouldRemoveNoDashboard() {
        dashboardDB.storeDashboard(dashboard1);
        dashboardDB.storeDashboard(dashboard2);

        dashboardDB.delete(new Dashboard());

        assertTrue(dashboardDB.existsDashboardByEditKey(dashboard1.getEditKey()));
        assertTrue(dashboardDB.existsDashboardByEditKey(dashboard1.getEditKey()));
    }

    @Test
    void delete_DashboardExists_ShouldRemoveDashboard() {
        dashboardDB.storeDashboard(dashboard1);
        dashboardDB.storeDashboard(dashboard2);


        dashboardDB.delete(dashboard2);

        assertTrue(dashboardDB.existsDashboardByEditKey(dashboard1.getEditKey()));
        assertFalse(dashboardDB.existsDashboardByEditKey(dashboard2.getEditKey()));
    }

    @Test
    void existsDashboardByViewKey_DashboardExists_ShouldReturnTrue() {
        dashboardDB.storeDashboard(dashboard1);
        dashboardDB.storeDashboard(dashboard2);


        assertTrue(dashboardDB.existsDashboardByViewKey(dashboard1.getViewKey()));
        assertTrue(dashboardDB.existsDashboardByViewKey(dashboard2.getViewKey()));
    }

    @Test
    void existsDashboardByViewKey_DashboardDoesNotExist_ShouldReturnFalse() {
        dashboardDB.storeDashboard(dashboard1);
        dashboardDB.storeDashboard(dashboard2);


        assertFalse(dashboardDB.existsDashboardByViewKey("another random view key."));
    }

    @Test
    void existsDashboardByEditKey_DashboardExists_ShouldReturnTrue() {
        dashboardDB.storeDashboard(dashboard1);
        dashboardDB.storeDashboard(dashboard2);


        assertTrue(dashboardDB.existsDashboardByEditKey(dashboard1.getEditKey()));
        assertTrue(dashboardDB.existsDashboardByEditKey(dashboard2.getEditKey()));
    }

    @Test
    void existsDashboardByEditKey_DashboardDoesNotExist_ShouldReturnFalse() {
        dashboardDB.storeDashboard(dashboard1);
        dashboardDB.storeDashboard(dashboard2);


        assertFalse(dashboardDB.existsDashboardByEditKey("another random edit key."));
    }


}
