package ch.bfh.exit_simulation.model;

import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.List;

/**
 * An obstacle in the scene where entities have to walk around.
 *
 * Created by lukas on 15.10.15.
 */
public interface IObstacle {
    /**
     * An entity has to walk around an obstacle. The navigation points help to find a path around the obstacle.
     * The navigation points should be place a bit outside the edges to achieve an efficient path finding.
     * @return navigation points around the obstacle
     */
    public List<Vector2d> getNavigationPoints();

    /**
     * Tests if a line collides with the obstacle.
     * @param line Line to check against.
     * @return True if line collides with obstacle, false if it doesn't.
     */
    public boolean collides(Line2D line);

    /**
     * Gets minimal distance between
     * @param point
     * @return
     */
    public double getDistance(Vector2d point);

    /**
     * Get the closest point on the obstacle starting from p.
     * @param p starting point
     * @return Closest point on the obstacle.
     */
    public Vector2d getClosestPoint(Vector2d p);
}
