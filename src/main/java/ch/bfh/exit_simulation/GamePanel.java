package ch.bfh.exit_simulation;

import ch.bfh.exit_simulation.controller.AttractionNavigator;
import ch.bfh.exit_simulation.controller.BallController;
import ch.bfh.exit_simulation.controller.INavigator;
import ch.bfh.exit_simulation.controller.PreBuiltPathFinder;
import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.model.Exit;
import ch.bfh.exit_simulation.model.IObstacle;
import ch.bfh.exit_simulation.model.ObstaclePoly;
import ch.bfh.exit_simulation.util.Vector2d;
import ch.bfh.exit_simulation.view.BallRenderer;
import ch.bfh.exit_simulation.view.ObstaclePolyRenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vincent Genecand on 21.09.2015.
 */
public class GamePanel implements MouseListener, MouseMotionListener {

    private Set<Ball> balls;
    public Set<ObstaclePoly> obstacles;
    public Exit exit;
    private List<Line2D> obstacleNavLineCache;

    private PreBuiltPathFinder pathfinder;
    private AttractionNavigator attractionNavigator;

    private static GamePanel _instance;
    public static GamePanel getInstance() {
        if (_instance == null)
            _instance = new GamePanel();
        return _instance;
    }
    private GamePanel() {
        this.balls = new HashSet<>();
        this.obstacles = new HashSet<>();
        this.exit = new Exit(SimulationCanvas.W - 10, SimulationCanvas.H / 2, 10, 50);

//        IntStream.range(0,10).forEach(x -> this.balls.add(Ball.createGenericBall(this.balls.size())));
//        IntStream.range(0,50).forEach(x -> this.balls.add(Ball.createRandomBall()));
        this.balls.addAll(Ball.createCardinalBalls());

        this.obstacles.addAll(ObstaclePoly.createDemoObstacles());

        this.pathfinder = new PreBuiltPathFinder(this);
        this.attractionNavigator = new AttractionNavigator();
    }

    public void update() {
        this.balls.forEach(ball -> new BallController(ball, this).update());
        List<Ball> ballsToCheck = new ArrayList<>(this.balls);
        for (Ball b : this.balls) {
            ballsToCheck.remove(b);
            ballsToCheck.forEach(ball -> new BallController(b, this).elasticCollision(ball));
        }
    }

    public void render(Graphics2D g, float interpolation) {
        this.balls.forEach(ball -> new BallRenderer(ball).render(g, interpolation));
        this.obstacles.forEach(obstacle -> new ObstaclePolyRenderer(obstacle).render(g, interpolation));
        g.setColor(Color.green);
        getObstacleNavigationLines().forEach(line -> g.draw(line));
        g.setColor(Color.BLUE);
        g.fill(this.exit);

        for (Ball b: this.balls) {
            g.setColor(Color.BLUE);
            List<Vector2d> path = pathfinder.getPathToExit(b.getCurrentPos());
            if (path == null) continue;
            for (int i = 0; i < path.size()-1; i++) {
                g.draw(new Line2D.Double(path.get(i).getPoint(), path.get(i+1).getPoint()));
            }
            Vector2d closestPointOnObst = getClosestPoint(b.getCurrentPos());
            g.setColor(Color.magenta);
            g.draw(new Line2D.Double(b.getCurrentPos().getPoint(), closestPointOnObst.getPoint()));
        }
//        for (Vector2d key: this.pathfinder.navTree.keySet()) {
//            Vector2d val = this.pathfinder.navTree.get(key);
//            if (val != null)
//                g.draw(new Line2D.Double(key.getPoint(), val.getPoint()));
//        }
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
     * @param start Current position to search from.
     * @return The closest point to the start on an obstacle.
     */
    public Vector2d getClosestPoint(Vector2d start) {
        Vector2d closestPoint = Vector2d.MAX;
        for (IObstacle obstacle: this.obstacles) {
            Vector2d pointOnObst = obstacle.getClosestPoint(start);
            if (start.distance(closestPoint) > start.distance(pointOnObst))
                closestPoint = pointOnObst;
        }
        return closestPoint;
    }

    public double getDistanceToClosestObject(Vector2d start) {
        return getClosestPoint(start).distance(start);
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
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}
