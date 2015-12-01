package ch.bfh.exit_simulation.view;

import java.awt.*;
import java.lang.reflect.Field;

/**
 * Created by Vincent Genecand on 23.09.2015.
 */
public interface Renderer {

    void render(Graphics2D graphics, float interpolation);

    static Color getColorFromName(String colorName) {
        try {
            Field field = Class.forName("java.awt.Color").getField(colorName);
            return (Color) field.get(null);
        } catch (Exception e) {
            throw new Error(String.format("Color '%s' not found.", colorName));
        }
    }
}
