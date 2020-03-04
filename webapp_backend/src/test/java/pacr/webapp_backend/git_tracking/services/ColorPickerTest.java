package pacr.webapp_backend.git_tracking.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for ColorPicker.
 *
 * @author Pavel Zwerschke
 */
public class ColorPickerTest {

    private final static String COLOR_1 = "#ff123a";
    private final static String COLOR_2 = "#bbaa44";
    private final static String COLOR_3 = "#00001a";
    private final static List<String> COLORS = Arrays.asList(COLOR_1, COLOR_2, COLOR_3);

    private ColorPicker colorPicker;

    @BeforeEach
    public void setUp() {
        colorPicker = new ColorPicker(COLORS);
    }

    @Test
    public void getColors() {
        final String color1 = colorPicker.getNextColor();
        final String color2 = colorPicker.getNextColor();
        final String color3 = colorPicker.getNextColor();

        assertEquals(COLOR_1, color1);
        assertEquals(COLOR_2, color2);
        assertEquals(COLOR_3, color3);
    }

    @Test
    public void setColorToUnused() {
        final String color = colorPicker.getNextColor();
        colorPicker.getNextColor();
        colorPicker.setColorToUnused(color);

        assertEquals(color, colorPicker.getNextColor());
    }

    @Test
    public void setColorToUsed() {
        colorPicker.setColorToUsed(COLOR_1);

        assertEquals(COLOR_2, colorPicker.getNextColor());
    }

}
