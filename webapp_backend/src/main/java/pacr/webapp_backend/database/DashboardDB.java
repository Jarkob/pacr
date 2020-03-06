package pacr.webapp_backend.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import pacr.webapp_backend.dashboard_management.Dashboard;
import pacr.webapp_backend.dashboard_management.services.IDashboardAccess;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * This interface allows access to a database regarding entities,
 * which are managed in the dashboard_management component.
 * For this, it contains jpa auto-generated methods and default implementations.
 */
@Component
public interface DashboardDB extends CrudRepository<Dashboard, Integer>, IDashboardAccess {

    @Override
    Dashboard findByEditKey(String editKey);

    @Override
    Dashboard findByViewKey(String viewKey);

    @Override
    Collection<Dashboard> findAll();


    @Override
    default void storeDashboard(@NotNull final Dashboard dashboard) {
        this.save(dashboard);
    }

    @Override
    void delete(Dashboard dashboard);

    @Override
    boolean existsDashboardByEditKey(String editKey);

    @Override
    boolean existsDashboardByViewKey(String viewKey);


}
