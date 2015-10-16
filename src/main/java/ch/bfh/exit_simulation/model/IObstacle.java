package ch.bfh.exit_simulation.model;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;

/**
 * Created by lukas on 15.10.15.
 */
public interface IObstacle {
    public List<Point> getNavigationPoints();
    public boolean collides(Line2D line);
}
