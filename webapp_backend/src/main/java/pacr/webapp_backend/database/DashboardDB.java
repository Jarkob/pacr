package pacr.webapp_backend.database;

import javassist.NotFoundException;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.services.IDashboardAccess;
import pacr.webapp_backend.result_management.BenchmarkGroup;

import javax.validation.constraints.NotNull;


//TODO
@Component
public interface DashboardDB extends CrudRepository<BenchmarkGroup, Integer>, IDashboardAccess {

    @Override
    default Dashboard getDashboard(@NotNull String key) throws NotFoundException {
        return null;
    }

    @Override
    default void addDashboard(@NotNull Dashboard dashboard) {

    }

    @Override
    default void deleteDashboard(@NotNull int id) throws NotFoundException {

    }

    @Override
    default void deleteDashboard(@NotNull String editKey) throws NotFoundException {

    }

    @Override
    default int checkKeyType(String key) {
        return 0;
    }

    @Override
    default void setDeletionInterval(int deletionInterval) {

    }

    @Override
    default int getDeletionInterval() {
        return 0;
    }
}
