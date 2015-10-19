package ch.bfh.exit_simulation.model;

import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.*;
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

    @Override
    public boolean collides(Line2D line) {
        for (Line2D border: this.getBorderLines()) {
            if (line.intersectsLine(border))
                return true;
        }
        return false;
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
    private void addRelativePoint(double x, double y) {
        this.addPoint(new Double(SimulationCanvas.W * x).intValue(), new Double(SimulationCanvas.H*y).intValue());
    }
}
