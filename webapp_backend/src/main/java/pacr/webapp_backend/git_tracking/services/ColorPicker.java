package pacr.webapp_backend.git_tracking.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.awt.Color;
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
    private Map<Color, Boolean> colors;
    private List<Color> colorList;

    public ColorPicker(@NotNull @Value("#{'${repository.colors}'.split(',')}") List<String> colors) {
        Objects.requireNonNull(colors);

        this.colors = new HashMap<>();
        this.colorList = new ArrayList<>();

        for (String colorStr : colors) {
            Color color = new Color(Integer.parseInt(colorStr, 16));
            this.colors.put(color, Boolean.TRUE);
            this.colorList.add(color);
        }
    }

    @Override
    public Color getNextColor() {
        for (Color color : colorList) {
            if (colors.get(color)) {
                colors.put(color, Boolean.FALSE);
                return color;
            }
        }
        // if all colors are used, generate new random color
        return new Color((int) (Math.random() * 0xFFFFFF));
    }

    @Override
    public void setColorToUnused(@NotNull Color color) {
        Objects.requireNonNull(color);

        colors.put(color, Boolean.TRUE);
    }

    @Override
    public void setColorToUsed(@NotNull Color color) {
        Objects.requireNonNull(color);

        colors.put(color, Boolean.FALSE);
    }

}
