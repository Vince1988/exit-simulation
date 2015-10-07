package ch.bfh.exit_simulation;

import ch.bfh.exit_simulation.controller.BallController;
import ch.bfh.exit_simulation.controller.Controller;
import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.view.BallRenderer;
import com.sun.javafx.geom.Vec2d;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vincent Genecand on 21.09.2015.
 */
public class GamePanel {

    private Set<Ball> balls;

    public GamePanel() {
        this.balls = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            this.balls.add(Ball.createRandomBall());
        }

    }

    private int rndColor() {
        return (int) (Math.random()  * 255);
    }

    public void update() {
        this.balls.forEach(ball -> new BallController(ball).update());
    }

    public void render(Graphics2D g, float interpolation) {
        this.balls.forEach(ball -> new BallRenderer(ball).render(g, interpolation));
    }
}
