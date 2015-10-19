package ch.bfh.exit_simulation.view;

import ch.bfh.exit_simulation.model.ObstaclePoly;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.*;
import java.awt.geom.Line2D;

/**
 * Created by Shylux on 13.10.2015.
 */
public class ObstaclePolyRenderer implements Renderer {

    private static final int CENTER_POINT_SIZE = 10;
    private static final int NAVIGATION_POINT_SIZE = 10;
    private final ObstaclePoly model;

    public ObstaclePolyRenderer(ObstaclePoly mod) {
        this.model = mod;
    }

    @Override
    public void render(Graphics2D graphics, float interpolation) {
        graphics.drawPolygon(model);
        Point centP = model.centerPoint();
        graphics.drawOval(
                new Double(centP.getX()).intValue() - CENTER_POINT_SIZE/2,
                new Double(centP.getY()).intValue() - CENTER_POINT_SIZE/2,
                CENTER_POINT_SIZE,
                CENTER_POINT_SIZE);
        Point center = model.centerPoint();

        for (Vector2d p: model.getNavigationPoints()) {
            graphics.draw(new Line2D.Double(center.getX(), center.getY(), p.getX(), p.getY()));
            graphics.setColor(Color.green);
            graphics.fillOval(
                    new Double(p.getX()).intValue() - NAVIGATION_POINT_SIZE / 2,
                    new Double(p.getY()).intValue() - NAVIGATION_POINT_SIZE / 2,
                    NAVIGATION_POINT_SIZE,
                    NAVIGATION_POINT_SIZE);
            graphics.setColor(Color.black);
        }
    }
}
