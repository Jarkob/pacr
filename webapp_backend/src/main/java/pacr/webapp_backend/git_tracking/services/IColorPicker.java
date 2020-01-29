package pacr.webapp_backend.git_tracking.services;

import javax.validation.constraints.NotNull;

/**
 * Assigns new colors to repositories.
 * Keeps track of used colors so that no color gets used multiple times.
 *
 * @author Pavel Zwerschke
 */
public interface IColorPicker {

    /**
     * Gets the next available color in the color list for repositories.
     * @return next color.
     */
    String getNextColor();

    /**
     * Sets a color to unused in the color list for repositories.
     * @param color gets set to unused.
     */
    void setColorToUnused(@NotNull String color);

    /**
     * Sets a color to used in the color list for repositories.
     * @param color gets set to used.
     */
    void setColorToUsed(@NotNull String color);

}
