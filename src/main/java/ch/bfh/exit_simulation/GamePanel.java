package ch.bfh.exit_simulation;

import ch.bfh.exit_simulation.controller.BallController;
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
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vincent Genecand on 21.09.2015.
 */
public class GamePanel {

    private Set<Ball> balls;
    public Set<ObstaclePoly> obstacles;
    public Exit exit;
    private List<Line2D> obstacleNavLineCache;

    private PreBuiltPathFinder pathfinder;

    public GamePanel() {
        this.balls = new HashSet<>();
        this.obstacles = new HashSet<>();
        this.exit = new Exit(SimulationCanvas.W - 10, SimulationCanvas.H / 2, 10, 50);

//        IntStream.range(0,10).forEach(x -> this.balls.add(Ball.createGenericBall(this.balls.size())));
//        IntStream.range(0,50).forEach(x -> this.balls.add(Ball.createRandomBall()));
        this.balls.addAll(Ball.createCardinalBalls());

        this.obstacles.addAll(ObstaclePoly.createDemoObstacles());

        this.pathfinder = new PreBuiltPathFinder(this);
    }

    public void update() {
        this.balls.forEach(ball -> new BallController(ball).update());
        List<Ball> ballsToCheck = new ArrayList<>(this.balls);
        for (Ball b : this.balls) {
            ballsToCheck.remove(b);
            ballsToCheck.forEach(ball -> new BallController(b).elasticCollision(ball));
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
            List<Vector2d> path = pathfinder.getPathToExit(b.getCurrentPos());
            if (path == null) continue;
            for (int i = 0; i < path.size()-1; i++) {
                g.draw(new Line2D.Double(path.get(i).getPoint(), path.get(i+1).getPoint()));
            }
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
}
