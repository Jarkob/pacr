package pacr.webapp_backend.dashboard_management;


import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Instances of this class model dashboards, which contain dashboard modules
 * and are displayed to the user in the frontend.
 * Those instances furthermore have a title and contain edit and view keys,
 * with which they can be retrieved and edited from the frontend.
 *
 * Public methods of this class can be used to remove and add modules,
 * as well as edit keys.
 *
 * @author Benedikt Hahn
 */
@Entity(name = "Dashboard")
@Table(name = "dashboard")
@NoArgsConstructor
public class Dashboard {

    static final int SIZE = 15;

    @Id
    @GeneratedValue
    private int id;

    private LocalDate lastAccess = LocalDate.now();

    private String editKey;
    private String viewKey;

    private String title;

    //Limited to 15 positions on the dashboard.
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)

    //List because arrays cannot be stored by JPA
    @Size(min = 0, max = SIZE)
    private final List<DashboardModule> modules = new ArrayList<>(SIZE);


    /**
     * Creates a new dashboard with a set title.
     * Throws an illegal argument exception, if the string is null, empty or blank.
     *
     * @param title The title of the dashboard.
     */
    public Dashboard(@NotNull final String title) {
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("The dashboard title '" + title + "' must not be null, empty or blank.");
        }
        this.title = title;
    }

    /**
     * Adds a module to the dashboard, if the dashboard is not full.
     *
     * @param module The module that will be added.
     */
    public void addModule(@NotNull final DashboardModule module) {

        Objects.requireNonNull(module, "The dashboard module must not be null.");

        if (modules.size() == SIZE) {
            throw new DashboardFullException();
        }

        modules.add(module);
    }

    /**
     * Adds a module to the dashboard at the specified position, if the dashboard is not full.
     *
     * @param module   The module that will be added.
     * @param position The position, at which the module will be added.
     */
    public void addModule(@NotNull final DashboardModule module, final int position) {
        Objects.requireNonNull(module, "The dashboard module must not be null.");

        if (modules.size() == SIZE) {
            throw new DashboardFullException();
        }

        modules.add(position, module);
    }

    /**
     * Removes the specified module from the dashboard and returns whether the remove operation
     * was successful.
     *
     * @param module The module to be removed.
     * @return {@code true} if the module was removed and {@code false} else.
     */
    public boolean removeModule(@NotNull final DashboardModule module) {
        Objects.requireNonNull(module, "The dashboard module must not be null.");

        return modules.remove(module);
    }

    /**
     * Removes the module at the given position and returns whether the remove operation
     * was successful.
     *
     * @param position The position to remove the module from.
     * @return {@code true} if the module at position could be removed and {@code false} else.
     */
    public boolean removeModule(final int position) {
        if (position >= SIZE || position < 0) {
            throw new IndexOutOfBoundsException("Dashboard modules can only be positioned in the range"
                    + " [0," + (SIZE - 1) + "].");
        }

        try {
            modules.remove(position);
        } catch (final IndexOutOfBoundsException e) {
            return false;
        }
        return true;
    }

    /**
     * Sets the keys of this dashboard.
     *
     * @param editKey The new edit key.
     * @param viewKey The new view key.
     * @throws KeysAlreadyInitializedException if this method is called, when the keys are already initialized.
     */
    public void initializeKeys(final String editKey, final String viewKey) throws KeysAlreadyInitializedException {
        if (this.editKey != null && this.viewKey != null) {
            throw new KeysAlreadyInitializedException("The keys have already been initialized.");
        } else {
            Objects.requireNonNull(editKey, "The edit key must not be null.");
            Objects.requireNonNull(viewKey, "The view key must not be null.");

            this.editKey = editKey;
            this.viewKey = viewKey;
        }
    }

    /**
     * Prepares the dashboard for sending it to the frontend, when it was not
     * accessed via a view key.
     * In this case, its edit key gets removed.
     */
    public void prepareForViewAccess() {
        this.editKey = null;
    }

    /**
     * @return the edit key of this dashboard. The value can be null.
     */
    public String getEditKey() {
        return editKey;
    }

    /**
     * @return the view key of this dashboard. The value can be null.
     */
    public String getViewKey() {
        return viewKey;
    }

    /**
     * @return the title of this dashboard.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the last access to now.
     */
    public void updateLastAccess() {
        this.lastAccess = LocalDate.now();
    }

    /**
     * @return the last time this dashboard was accessed.
     */
    public LocalDate getLastAccess() {
        return lastAccess;
    }

    /**
     * @return the unique id of this dashboard.
     */
    public int getId() {
        return id;
    }


    @Override
    public boolean equals(final Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || Dashboard.class != o.getClass()) {
            return false;
        }

        final Dashboard otherDashboard = (Dashboard) o;


        //Are the titles equal?
        if (this.title != null && !this.title.equals(otherDashboard.title)
                || otherDashboard.title != null && !otherDashboard.title.equals(this.title)) {

            return false;
        }


        //Are the view keys equal?
        if (this.viewKey != null && !this.viewKey.equals(otherDashboard.viewKey)
                || otherDashboard.viewKey != null && !otherDashboard.viewKey.equals(this.viewKey)) {

            return false;
        }


        //Are the edit keys equal?
        if (this.editKey != null && !this.editKey.equals(otherDashboard.editKey)
                || otherDashboard.editKey != null && !otherDashboard.editKey.equals(this.editKey)) {

            return false;
        }


        //Are the ids identical?
        if (this.id != otherDashboard.id) {
            return false;
        }


        //Is the last access equal?
        if (this.lastAccess != null && !this.lastAccess.equals(otherDashboard.lastAccess)
                || otherDashboard.lastAccess != null && !otherDashboard.lastAccess.equals(this.lastAccess)) {

            return false;
        }


        //Is one 'modules' bigger than the other?
        if (this.modules.size() != otherDashboard.modules.size()) {
            return false;
        }

        //Are the modules equal?
        for (int i = 0; i < this.modules.size(); i++) {
            if (!this.modules.get(i).equals(otherDashboard.modules.get(i))) {
                return false;
            }
        }

        return true;

    }
}
