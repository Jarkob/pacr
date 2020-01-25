package pacr.webapp_backend.git_tracking.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for ColorPicker.
 *
 * @author Pavel Zwerschke
 */
public class ColorPickerTest {

    private final static String COLOR_1 = "ff123a";
    private final static String COLOR_2 = "bbaa44";
    private final static String COLOR_3 = "00001a";
    private final static List<String> COLORS = Arrays.asList(COLOR_1, COLOR_2, COLOR_3);

    private ColorPicker colorPicker;

    @BeforeEach
    public void setUp() {
        colorPicker = new ColorPicker(COLORS);
    }

    @Test
    public void getColors() {
        Color color1 = colorPicker.getNextColor();
        Color color2 = colorPicker.getNextColor();
        Color color3 = colorPicker.getNextColor();

        assertEquals(new Color(Integer.parseInt(COLOR_1, 16)), color1);
        assertEquals(new Color(Integer.parseInt(COLOR_2, 16)), color2);
        assertEquals(new Color(Integer.parseInt(COLOR_3, 16)), color3);
    }

    @Test
    public void setColorToUnused() {
        Color color = colorPicker.getNextColor();
        colorPicker.getNextColor();
        colorPicker.setColorToUnused(color);
        assertEquals(color, colorPicker.getNextColor());
    }

    @Test
    public void setColorToUsed() {
        Color color = new Color(Integer.parseInt(COLOR_1, 16));
        colorPicker.setColorToUsed(color);
        assertEquals(new Color(Integer.parseInt(COLOR_2, 16)), colorPicker.getNextColor());
    }

}
