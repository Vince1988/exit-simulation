package ch.bfh.exit_simulation.model;

import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;

/**
 * Created by lukas on 15.10.15.
 */
public interface IObstacle {
    public List<Vector2d> getNavigationPoints();
    public boolean collides(Line2D line);
}
