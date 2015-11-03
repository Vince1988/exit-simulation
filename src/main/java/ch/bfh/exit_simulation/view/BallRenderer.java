package ch.bfh.exit_simulation.view;

import ch.bfh.exit_simulation.controller.BallController;
import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

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
        // Save currently used stroke and color, to be able to reset them at the end.
        Color defaultColor = graphics.getColor();
        Stroke defaultStroke = graphics.getStroke();

        // draw the ball
        graphics.setColor(ball.getColor());
        int drawX = (int) ((ball.getCurrentPos().getX() - ball.getLastPos().getX()) * interpolation + ball.getLastPos().getX());
        int drawY = (int) ((ball.getCurrentPos().getY() - ball.getLastPos().getY()) * interpolation + ball.getLastPos().getY());

        // draw the slow down ranges
        graphics.setColor(new Color(1f, 0f, 0f, 0.5f));
        int crawl_radius = (int)(ball.getRadius() * (BallController.CRAWL_DISTANCE+1));
        graphics.fillOval(drawX-crawl_radius, drawY-crawl_radius, 2*crawl_radius, 2*crawl_radius);
        graphics.setColor(new Color(1f, 0f, 0f, 0.25f));
        int careful_radius = (int)(ball.getRadius() * (BallController.CAREFUL_DISTANCE+1));
        graphics.fillOval(drawX-careful_radius, drawY-careful_radius, 2*careful_radius, 2*careful_radius);

        graphics.setColor(defaultColor);
        graphics.fillOval(drawX-ball.getRadius(), drawY-ball.getRadius(), ball.getRadius() * 2, ball.getRadius() * 2);

//        this.renderSpeedVector(drawX, drawY, graphics);

        // Reset stroke and color to default
        graphics.setStroke(defaultStroke);
        graphics.setColor(defaultColor);
    }

    private void renderSpeedVector(int drawX, int drawY, Graphics2D graphics) {
        // Create a line showing the direction and speed (length) of the ball
        int x1 = drawX + ball.getRadius();
        int y1 = drawY + ball.getRadius();
        Vector2d speed = ball.getSpeed().scale(4);
        int x2 = (int) (x1 + speed.getX());
        int y2 = (int) (y1 + speed.getY());

        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawLine(x1, y1, x2, y2);
    }

}
