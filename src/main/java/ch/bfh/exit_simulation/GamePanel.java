package ch.bfh.exit_simulation;

import ch.bfh.exit_simulation.controller.BallController;
import ch.bfh.exit_simulation.controller.Collision;
import ch.bfh.exit_simulation.controller.CollisionAnalyzer;
import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.model.IObstacle;
import ch.bfh.exit_simulation.model.ObstaclePoly;
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
    private Set<ObstaclePoly> obstacles;
    private List<Line2D> obstacleNavLineCache;

    public GamePanel() {
        this.balls = new HashSet<>();
        this.obstacles = new HashSet<>();

//        IntStream.range(0,10).forEach(x -> this.balls.add(Ball.createGenericBall(this.balls.size())));
//        IntStream.range(0,50).forEach(x -> this.balls.add(Ball.createRandomBall()));
        this.balls.addAll(Ball.createCardinalBalls());

        this.obstacles.addAll(ObstaclePoly.createDemoObstacles());
    }

    public void update() {
        this.balls.forEach(ball -> new BallController(ball).update());
        List<Ball> ballsToCheck = new ArrayList<>(this.balls);
        Set<Collision> collisions = new HashSet<>();
        this.balls.forEach(ball -> {
            BallController c = new BallController(ball);
            ballsToCheck.remove(ball);
            ballsToCheck.forEach(b -> collisions.add(c.highSpeedCollision(b)));
//            ballsToCheck.forEach(c::elasticCollision);
        });
        collisions.remove(null);
        CollisionAnalyzer.executeValidCollisions(collisions);
    }

    public void render(Graphics2D g, float interpolation) {
        this.balls.forEach(ball -> new BallRenderer(ball).render(g, interpolation));
        this.obstacles.forEach(obstacle -> new ObstaclePolyRenderer(obstacle).render(g, interpolation));
        g.setColor(Color.green);
        getObstacleNavigationLines().forEach(line -> g.draw(line));
    }

    public List<Line2D> getObstacleNavigationLines() {
        if (obstacleNavLineCache != null)
            return obstacleNavLineCache;

        ArrayList<Line2D> lst = new ArrayList<>();
        ArrayList<Point> navPoints = new ArrayList<>();
        this.obstacles.forEach(obstacle -> navPoints.addAll(obstacle.getNavigationPoints()));
        for (int i = 0; i < navPoints.size(); i++) {
            for (int j = i + 1; j < navPoints.size(); j++) {
                lst.add(new Line2D.Double(navPoints.get(i), navPoints.get(j)));
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
