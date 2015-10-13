package ch.bfh.exit_simulation.model;

import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by Shylux on 13.10.2015.
 */
public class ObstaclePoly extends Polygon {


    public Point centerPoint() {
        int x = IntStream.of(this.xpoints).limit(this.npoints).sum() / this.npoints;
        int y = IntStream.of(this.ypoints).limit(this.npoints).sum() / this.npoints;
        return new Point(x, y);
    }
    public List<Point> getNavigationPoints() {
        ArrayList<Point> lst = new ArrayList<Point>();
        Vector2d center = new Vector2d(this.centerPoint());
        for (int i = 0; i < this.npoints; i++) {
            Vector2d corner = new Vector2d(this.xpoints[i], this.ypoints[i]);
            Vector2d direction = corner.sub(center);
            Vector2d navVector = direction.scale(1.2);
            Vector2d navPoint = center.add(navVector);
            lst.add(new Point(new Double(navPoint.getX()).intValue(), new Double(navPoint.getY()).intValue()));
        }
        return lst;
    }
    public static ObstaclePoly createDemoObstacle() {
        ObstaclePoly op = new ObstaclePoly();
        op.addPoint(new Double(SimulationCanvas.W*.35).intValue(), new Double(SimulationCanvas.H*.35).intValue());
        op.addPoint(new Double(SimulationCanvas.W*.45).intValue(), new Double(SimulationCanvas.H*.65).intValue());
        op.addPoint(new Double(SimulationCanvas.W*.75).intValue(), new Double(SimulationCanvas.H*.55).intValue());
        op.addPoint(new Double(SimulationCanvas.W*.55).intValue(), new Double(SimulationCanvas.H*.35).intValue());
        op.addPoint(new Double(SimulationCanvas.W*.50).intValue(), new Double(SimulationCanvas.H*.25).intValue());
        return op;
    }
}
