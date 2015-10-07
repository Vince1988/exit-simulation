package ch.bfh.exit_simulation.view;

import ch.bfh.exit_simulation.model.Ball;

import java.awt.*;

/**
 * Created by Vincent Genecand on 05.10.2015.
 */
public class BallRenderer implements Renderer {

    private final Ball ball;

    public BallRenderer(Ball ball) {
        this.ball = ball;
    }

    @Override
    public void render(Graphics2D graphics, float interpolation) {
        Color c = graphics.getColor();
        graphics.setColor(ball.getColor());
        int drawX = (int) ((ball.getCurrentX() - ball.getLastX()) * interpolation + ball.getLastX() - ball.getRadius() / 2);
        int drawY = (int) ((ball.getCurrentY() - ball.getLastY()) * interpolation + ball.getLastY() - ball.getRadius() / 2);
        graphics.fillOval(drawX, drawY, ball.getRadius(), ball.getRadius());
        graphics.setColor(c);
    }

}
