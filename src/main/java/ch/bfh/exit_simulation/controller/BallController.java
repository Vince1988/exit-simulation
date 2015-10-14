package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.util.Vector2d;

/**
 * Created by Vincent Genecand on 05.10.2015.
 */
public class BallController implements Controller {

    private final Ball ball;

    public BallController(Ball ball) {
        this.ball = ball;
    }

    @Override
    public void update() {
        ball.setLastPos(ball.getCurrentPos());
        ball.setCurrentPos(ball.getCurrentPos().add(ball.getSpeed()));

        if (ball.getCurrentPos().getX() + ball.getRadius() / 2 >= SimulationCanvas.W) {
            ball.setSpeed(ball.getSpeed().reflect(new Vector2d(0, 1)));
            ball.setCurrentPos(new Vector2d(SimulationCanvas.W - ball.getRadius(), ball.getCurrentPos().getY()));
        } else if (ball.getCurrentPos().getX() - ball.getRadius() <= 0) {
            ball.setSpeed(ball.getSpeed().reflect(new Vector2d(0, 1)));
            ball.setCurrentPos(new Vector2d(ball.getRadius(), ball.getCurrentPos().getY()));
        }

        if (ball.getCurrentPos().getY() + ball.getRadius() >= SimulationCanvas.H) {
            ball.setSpeed(ball.getSpeed().reflect(new Vector2d(1, 0)));
            ball.setCurrentPos(new Vector2d(ball.getCurrentPos().getX(), SimulationCanvas.H - ball.getRadius()));
        } else if (ball.getCurrentPos().getY() - ball.getRadius() <= 0) {
            ball.setSpeed(ball.getSpeed().reflect(new Vector2d(1, 0)));
            ball.setCurrentPos(new Vector2d(ball.getCurrentPos().getX(), ball.getRadius()));
        }
    }
}
