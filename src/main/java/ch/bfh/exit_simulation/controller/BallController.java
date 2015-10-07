package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.model.Ball;

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
        ball.setLastX(ball.getCurrentX());
        ball.setLastY(ball.getCurrentY());

        ball.setCurrentX(ball.getCurrentX() + ball.getSpeedX());
        ball.setCurrentY(ball.getCurrentY() + ball.getSpeedY());

        if (ball.getCurrentX() + ball.getRadius() / 2 >= SimulationCanvas.W) {
            ball.setSpeedX(ball.getSpeedX() * -1);
            ball.setCurrentX(SimulationCanvas.W - ball.getRadius() / 2);
//            ball.setSpeedY((float) Math.random() * ball.getSpeed() * 2 - ball.getSpeed());
        } else if (ball.getCurrentX() - ball.getRadius() / 2 <= 0) {
            ball.setSpeedX(ball.getSpeedX() * -1);
            ball.setCurrentX(ball.getRadius() / 2);
        }

        if (ball.getCurrentY() + ball.getRadius() / 2 >= SimulationCanvas.H) {
            ball.setSpeedY(ball.getSpeedY() * -1);
            ball.setCurrentY(SimulationCanvas.H - ball.getRadius() / 2);
//            ball.setSpeedX((float) Math.random() * ball.getSpeed() * 2 - ball.getSpeed());
        } else if (ball.getCurrentY() - ball.getRadius() / 2 <= 0) {
            ball.setSpeedY(ball.getSpeedY() * -1);
            ball.setCurrentY(ball.getRadius() / 2);
        }
    }
}
