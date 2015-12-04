package ch.bfh.exit_simulation;

import ch.bfh.exit_simulation.controller.AttractionNavigator;
import ch.bfh.exit_simulation.controller.BallController;
import ch.bfh.exit_simulation.controller.Controller;
import ch.bfh.exit_simulation.controller.INavigator;
import ch.bfh.exit_simulation.controller.PreBuiltPathFinder;
import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.model.Exit;
import ch.bfh.exit_simulation.model.IObstacle;
import ch.bfh.exit_simulation.model.ObstacleBoundarie;
import ch.bfh.exit_simulation.model.ObstaclePoly;
import ch.bfh.exit_simulation.util.Vector2d;
import ch.bfh.exit_simulation.view.BallRenderer;
import ch.bfh.exit_simulation.view.ObstaclePolyRenderer;
import ch.bfh.exit_simulation.view.Renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Vincent Genecand on 21.09.2015.
 */
public class GamePanel implements MouseListener, MouseMotionListener {

    private Set<Controller<?>> controllers;
    private Set<Ball> balls;
    public List<IObstacle> obstacles;
    public Exit exit;
    private List<Line2D> obstacleNavLineCache;

    private PreBuiltPathFinder pathfinder;
    private AttractionNavigator attractionNavigator;

    public Properties props;

    private static GamePanel _instance;

    public static GamePanel getInstance() {
        if (_instance == null)
            _instance = new GamePanel();
        return _instance;
    }

    private GamePanel() {
        // read properties
        props = new Properties();
        try {
            InputStream in = GamePanel.class.getResourceAsStream("exitsim.properties");
            props.load(in);
        } catch (IOException e) {
            throw new Error("exitsim.properties not valid!");
        }

        this.controllers = new HashSet<>();
        this.balls = new HashSet<>();
        this.obstacles = new ArrayList<>();
        this.exit = new Exit(SimulationCanvas.W - 10, (int) (SimulationCanvas.H * 0.75), 10, 50);

//        IntStream.range(0,10).forEach(x -> this.balls.add(Ball.createGenericBall(this.balls.size())));
//        IntStream.range(0,50).forEach(x -> this.balls.add(Ball.createRandomBall()));

        this.obstacles.addAll(ObstaclePoly.createHallway());
        this.obstacles.addAll(ObstacleBoundarie.getGameBoundaries());

        this.pathfinder = new PreBuiltPathFinder(this);
        this.attractionNavigator = new AttractionNavigator();

        int ball_count = Integer.parseInt(props.getProperty("ballCount"));
        BallRenderer br = new BallRenderer();
        Ball.placeRandomBalls(ball_count, this, this.pathfinder).forEach(b -> this.controllers.add(new BallController(b, br)));


    }

    public void update() {
        this.controllers.forEach(Controller::updateModel);


        List<Ball> ballsToCheck = new ArrayList<>(this.balls);
//        for (Ball b : this.balls) {
//            ballsToCheck.remove(b);
//            ballsToCheck.forEach(ball -> new BallController(b, this).elasticCollision(ball));
//        }
    }

    public void render(Graphics2D g, float interpolation) {
        this.controllers.forEach(controller -> controller.updateView(g, interpolation));
        for (IObstacle obst : obstacles) {
            if (obst instanceof ObstaclePoly)
                new ObstaclePolyRenderer((ObstaclePoly) obst).render((ObstaclePoly) obst, g, interpolation);
        }

        if (Boolean.parseBoolean(props.getProperty("renderNavigationLines"))) {
            g.setColor(Renderer.getColorFromName(props.getProperty("navigationLineColor")));
            getObstacleNavigationLines().forEach(line -> g.draw(line));
        }

        g.setColor(Color.BLUE);
        g.fill(this.exit);

        for (Ball b : this.balls) {
            if (Boolean.parseBoolean(props.getProperty("renderPath"))) {
                g.setColor(Renderer.getColorFromName(props.getProperty("exitPathColor")));
                List<Vector2d> path = pathfinder.getPathToExit(b.getCurrentPos());
                if (path == null) continue;
                for (int i = 0; i < path.size() - 1; i++) {
                    g.draw(new Line2D.Double(path.get(i).getPoint(), path.get(i + 1).getPoint()));
                }
            }
            if (Boolean.parseBoolean(props.getProperty("renderLineToClosestObject"))) {
                Vector2d closestPointOnObst = getClosestEntity(b.getCurrentPos());
                g.setColor(Renderer.getColorFromName(props.getProperty("lineToClosestObjectColor")));
                g.draw(new Line2D.Double(b.getCurrentPos().getPoint(), closestPointOnObst.getPoint()));
            }
        }
    }

    public List<Line2D> getObstacleNavigationLines() {
        if (obstacleNavLineCache != null)
            return obstacleNavLineCache;

        ArrayList<Line2D> lst = new ArrayList<>();
        ArrayList<Vector2d> navPoints = new ArrayList<>();
        this.obstacles.forEach(obstacle -> navPoints.addAll(obstacle.getNavigationPoints()));

        for (int i = 0; i < navPoints.size(); i++) {
            for (int j = i + 1; j < navPoints.size(); j++) {
                lst.add(new Line2D.Double(navPoints.get(i).getPoint(), navPoints.get(j).getPoint()));
            }
        }
        ArrayList<Line2D> collFreeList = new ArrayList<>();
        for (Line2D line : lst) {
            boolean collides = false;
            for (IObstacle obst : this.obstacles) {
                if (obst.collides(line)) {
                    collides = true;
                    break;
                }
            }
            if (!collides)
                collFreeList.add(line);
        }
        obstacleNavLineCache = collFreeList;
        return collFreeList;
    }

    /**
     * Get closest point to an obstacle.
     *
     * @param start Current position to search from.
     * @return The closest point to the start on an obstacle.
     */
    public Vector2d getClosestObstaclePoint(Vector2d start) {
        Vector2d closestPoint = Vector2d.MAX;
        for (IObstacle obstacle : this.obstacles) {

            Vector2d pointOnObst = obstacle.getClosestPoint(start);
            if (start.distance(closestPoint) > start.distance(pointOnObst))
                closestPoint = pointOnObst;
        }
        return closestPoint;
    }

    public Vector2d getClosestEntity(Vector2d start) {
        return getClosestEntity(start, null);
    }

    public Vector2d getClosestEntity(Vector2d start, Vector2d ignoreSelf) {
        Vector2d closestPoint = getClosestObstaclePoint(start);
        for (Ball ball : balls) {
            if (ball.getCurrentPos().equals(ignoreSelf)) continue;
            if (ball.getCurrentPos().equals(start)) continue;
            if (ball.getCurrentPos().distance(start) < closestPoint.distance(start))
                closestPoint = ball.getCurrentPos();
        }
        return closestPoint;
    }

    public double getClosestEntityDistance(Vector2d start, Vector2d ignoreSelf) {
        return getClosestEntity(start, ignoreSelf).distance(start);
    }

    public INavigator getNavigator() {
        if (attractionEnabled) return attractionNavigator;
        return this.pathfinder;
    }


    boolean attractionEnabled = false;

    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            attractionNavigator.setAttract();
        else if (e.getButton() == MouseEvent.BUTTON3)
            attractionNavigator.setScatter();

        attractionEnabled = true;
    }

    public void mouseReleased(MouseEvent e) {
        attractionEnabled = false;
    }

    public void mouseDragged(MouseEvent e) {
        attractionNavigator.setAttractionPoint(new Vector2d(e.getX(), e.getY()));
    }

    // Unused events
    // @formatter:off
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    // @formatter:on
}
