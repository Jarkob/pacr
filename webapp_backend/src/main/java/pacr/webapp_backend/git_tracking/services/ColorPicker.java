package pacr.webapp_backend.git_tracking.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Implementation for IColorPicker.
 *
 * @author Pavel Zwerschke
 */
@Component
public class ColorPicker implements IColorPicker {

    // value true for unused, value false for used
    private final Map<String, Boolean> colors;
    private final List<String> colorList;

    /**
     * Creates an instance of ColorPicker.
     * @param colors is the list of available colors.
     */
    public ColorPicker(@NotNull @Value("#{'${repository.colors}'.split(',')}") final List<String> colors) {
        Objects.requireNonNull(colors);

        this.colors = new HashMap<>();
        this.colorList = new ArrayList<>();

        for (final String color : colors) {
            this.colors.put(color, Boolean.TRUE);
            this.colorList.add(color);
        }
    }

    @Override
    public String getNextColor() {
        for (final String color : colorList) {
            if (colors.get(color)) {
                colors.put(color, Boolean.FALSE);
                return color;
            }
        }
        // if all colors are used, generate new random color
        return "#" + (int) (Math.random() * 0xFFFFFF);
    }

    @Override
    public void setColorToUnused(@NotNull final String color) {
        Objects.requireNonNull(color);

        colors.put(color, Boolean.TRUE);
    }

    @Override
    public void setColorToUsed(@NotNull final String color) {
        Objects.requireNonNull(color);

        colors.put(color, Boolean.FALSE);
    }

}
