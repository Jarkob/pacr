package pacr.webapp_backend.dashboard_management.services;

/**
 * This enum contains all the different key types.
 *
 * @author Benedikt Hahn
 */
public enum KeyType {

    /**
     * An edit key, with which editing and deleting dashboard is allowed.
     */
    EDIT_KEY,

    /**
     * A key is a view key, if it grants access to a dashboard but not to the edit operations.
     */
    VIEW_KEY,

    /**
     * A key is invalid, if it is whether a valid view nor edit key.
     */
    INVALID_KEY


}
