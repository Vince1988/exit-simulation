package ch.bfh.exit_simulation;

import ch.bfh.exit_simulation.controller.BallController;
import ch.bfh.exit_simulation.controller.Controller;
import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.model.ObstaclePoly;
import ch.bfh.exit_simulation.view.BallRenderer;
import ch.bfh.exit_simulation.view.ObstaclePolyRenderer;
import com.sun.javafx.geom.Vec2d;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vincent Genecand on 21.09.2015.
 */
public class GamePanel {

    private Set<Ball> balls;
    private Set<ObstaclePoly> obstacles;

    public GamePanel() {
        this.balls = new HashSet<>();
        this.obstacles = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            this.balls.add(Ball.createRandomBall());
        }
        this.obstacles.add(ObstaclePoly.createDemoObstacle());

    }

    private int rndColor() {
        return (int) (Math.random()  * 255);
    }

    public void update() {
        this.balls.forEach(ball -> new BallController(ball).update());
    }

    public void render(Graphics2D g, float interpolation) {
        this.balls.forEach(ball -> new BallRenderer(ball).render(g, interpolation));
        this.obstacles.forEach(obstacle -> new ObstaclePolyRenderer(obstacle).render(g, interpolation));
    }
}
