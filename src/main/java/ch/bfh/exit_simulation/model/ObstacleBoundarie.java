package ch.bfh.exit_simulation.model;

import ch.bfh.exit_simulation.GamePanel;
import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shylux on 05.11.2015.
 */
public class ObstacleBoundarie extends Line2D.Double implements IObstacle {

    public ObstacleBoundarie(Vector2d start, Vector2d end) {
        super(start.getPoint(), end.getPoint());
    }

    public List<Vector2d> getNavigationPoints() {
        return new ArrayList<>();
    }

    public boolean collides(Line2D line) {
        return this.intersectsLine(line);
    }

    public double getDistance(Vector2d point) {
        return getClosestPoint(point).distance(point);
    }

    public Vector2d getClosestPoint(Vector2d p) {
        return Vector2d.getClosestPointOnLine(p, new Vector2d(this.getP1()), new Vector2d(this.getP2()));
    }

    public static List<ObstacleBoundarie> getGameBoundaries() {
        Vector2d tl = Vector2d.ZERO;
        Vector2d tr = new Vector2d(SimulationCanvas.W, 0);
        Vector2d bl = new Vector2d(0, SimulationCanvas.H);
        Vector2d br = new Vector2d(SimulationCanvas.W, SimulationCanvas.H);
        ArrayList<ObstacleBoundarie> bounds = new ArrayList<>();
        bounds.add(new ObstacleBoundarie(tl, tr));
        bounds.add(new ObstacleBoundarie(tr, br));
        bounds.add(new ObstacleBoundarie(br, bl));
        bounds.add(new ObstacleBoundarie(bl, tl));
        return bounds;
    }
}
