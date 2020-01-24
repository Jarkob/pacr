package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.services.IDashboardAccess;
import pacr.webapp_backend.dashboard_management.services.KeyType;
import pacr.webapp_backend.result_management.BenchmarkGroup;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


//TODO
@Component
public interface DashboardDB extends CrudRepository<Dashboard, Integer>, IDashboardAccess {

    @Override
    Dashboard findByEditKey(String editKey);

    @Override
    Dashboard findByViewKey(String viewKey);

    @Override
    default List<Dashboard> getAllDashboards() {
        return new ArrayList<Dashboard>();
    }

    @Override
    default void addDashboard(@NotNull Dashboard dashboard) {

    }

    @Override
    default void updateDashboard(@NotNull Dashboard dashboard) throws NoSuchElementException  {

    }

    @Override
    default void deleteDashboard(@NotNull String editKey) throws NoSuchElementException {

    }

    @Override
    default KeyType checkKeyType(String key) {
        return KeyType.INVALID_KEY;
    }

    @Override
    default void setDeletionInterval(long deletionInterval) {

    }

    @Override
    default long getDeletionInterval() {
        return 0;
    }
}
