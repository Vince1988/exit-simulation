package ch.bfh.exit_simulation.model;

import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Shylux on 13.10.2015.
 */
public class ObstaclePoly extends Polygon implements IObstacle {
    private static final double NAV_POINT_DISTANCE_FROM_OBSTACLE = 20;


    public Point centerPoint() {
        int x = IntStream.of(this.xpoints).limit(this.npoints).sum() / this.npoints;
        int y = IntStream.of(this.ypoints).limit(this.npoints).sum() / this.npoints;
        return new Point(x, y);
    }
    public List<Vector2d> getNavigationPoints() {
        ArrayList<Vector2d> lst = new ArrayList<Vector2d>();
        Vector2d center = new Vector2d(this.centerPoint());
        for (int i = 0; i < this.npoints; i++) {
            Vector2d corner = new Vector2d(this.xpoints[i], this.ypoints[i]);
            Vector2d direction = corner.sub(center);
            Vector2d navPoint = center.add(direction).add(direction.setMagnitude(NAV_POINT_DISTANCE_FROM_OBSTACLE));
            lst.add(new Vector2d(navPoint.getX(), navPoint.getY()));
        }
        return lst;
    }
    public List<Line2D> getBorderLines() {
        List<Line2D> lst = new ArrayList<>();
        for (int i = 0; i < this.npoints; i++) {
            Point2D a = new Point2D.Double(this.xpoints[i], this.ypoints[i]);
            int j = (i+1)%this.npoints;
            Point2D b = new Point2D.Double(this.xpoints[j], this.ypoints[j]);
            lst.add(new Line2D.Double(a, b));
        }
        return lst;
    }

    /**
     * Tests if a line collides with the obstacle.
     *
     * Note that this method doesn't detect lines which are completely inside the obstacle.
     * @param line Line to check against.
     * @return True if line collides with obstacle, false if it doesn't.
     */
    @Override
    public boolean collides(Line2D line) {
        for (Line2D border: this.getBorderLines()) {
            if (line.intersectsLine(border))
                return true;
        }
        return false;
    }

    @Override
    public double getDistance(Vector2d p) {
        Vector2d closestPoint = getClosestPoint(p);
        return p.distance(closestPoint);
    }

    /**
     * Get closest point on polygon starting from p.
     * This point can be on a side of the polygon.
     * @param p starting point
     * @return Closest point on polygon.
     */
    public Vector2d getClosestPoint(Vector2d p) {
        Line2D line = this.getClosestLine(p);
        return Vector2d.getClosestPointOnLine(p, new Vector2d(line.getP1()), new Vector2d(line.getP2()));
    }

    public Line2D getClosestLine(Vector2d p) {
        Vector2d closestPoint = null;
        Line2D closestLine = null;

        for (Line2D line: getBorderLines()) {
            Vector2d currPoint = Vector2d.getClosestPointOnLine(p, new Vector2d(line.getP1()), new Vector2d(line.getP2()));

            if (closestPoint == null || p.distance(currPoint) < p.distance(closestPoint)) {
                closestPoint = currPoint;
                closestLine = line;
            }
        }

        return closestLine;
    }

    public boolean contains(Vector2d p) {
        return !this.collides(new Line2D.Double(p.getPoint(), this.centerPoint()));
    }

    public static List<ObstaclePoly> createDemoObstacles() {
        ArrayList<ObstaclePoly> lst = new ArrayList<ObstaclePoly>();
        ObstaclePoly op = new ObstaclePoly();
        op.addRelativePoint(.35, .35);
        op.addRelativePoint(.45, .65);
        op.addRelativePoint(.65, .55);
        op.addRelativePoint(.55, .35);
        op.addRelativePoint(.50, .30);
        lst.add(op);
        op = new ObstaclePoly();
        op.addRelativePoint(.85, .05);
        op.addRelativePoint(.95, .10);
        op.addRelativePoint(.90, .15);
        op.addRelativePoint(.85, .10);
        lst.add(op);
        op = new ObstaclePoly();
        op.addRelativePoint(.80, .75);
        op.addRelativePoint(.90, .80);
        op.addRelativePoint(.80, .60);
        lst.add(op);
        op = new ObstaclePoly();
        op.addRelativePoint(.70, .20);
        op.addRelativePoint(.95, .25);
        op.addRelativePoint(.70, .30);
        lst.add(op);
        return lst;
    }
    public static List<ObstaclePoly> createHallway() {
        ArrayList<ObstaclePoly> lst = new ArrayList<ObstaclePoly>();
        ObstaclePoly op = new ObstaclePoly();
        op.addRelativePoint(.85, 1);
        op.addRelativePoint(.75, 1);
        op.addRelativePoint(.75, .3);
        op.addRelativePoint(.85, .3);
        lst.add(op);
        op = new ObstaclePoly();
        op.addRelativePoint(.65, 0);
        op.addRelativePoint(.55, 0);
        op.addRelativePoint(.55, .7);
        op.addRelativePoint(.65, .7);
        lst.add(op);
        op = new ObstaclePoly();
        op.addRelativePoint(.45, .4);
        op.addRelativePoint(.40, .4);
        op.addRelativePoint(.40, .8);
        op.addRelativePoint(.45, .8);
        lst.add(op);
        op = new ObstaclePoly();
        op.addRelativePoint(.2, 1);
        op.addRelativePoint(.3, 1);
        op.addRelativePoint(.3, .6);
        op.addRelativePoint(.2, .6);
        lst.add(op);
        return lst;
    }

    public void addRelativePoint(double x, double y) {
        this.addPoint(new Double(SimulationCanvas.W * x).intValue(), new Double(SimulationCanvas.H*y).intValue());
    }
}
