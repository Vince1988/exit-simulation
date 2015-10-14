package ch.bfh.exit_simulation.view;

import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.util.Vector2d;

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
        // Save currently used stroke and color, to be able to reset them at the end.
        Color defaultColor = graphics.getColor();
        Stroke defaultStroke = graphics.getStroke();

        // draw the ball
        graphics.setColor(ball.getColor());
        int drawX = (int) ((ball.getCurrentPos().getX() - ball.getLastPos().getX()) * interpolation + ball.getLastPos().getX() - ball.getRadius());
        int drawY = (int) ((ball.getCurrentPos().getY() - ball.getLastPos().getY()) * interpolation + ball.getLastPos().getY() - ball.getRadius());
        graphics.fillOval(drawX, drawY, ball.getRadius() * 2, ball.getRadius() * 2);

        // Display the ball as a ring
//        double offset = ball.getRadius() / 3;
//        int r = (int) (ball.getRadius() - offset);
//        int x = (int) (drawX + offset);
//        int y = (int) (drawY + offset);
//
//        graphics.setColor(graphics.getBackground());
//        graphics.fillOval(x,y,r * 2,r * 2);

        // Create a line showing the direction and speed (length) of the ball
        int x1 = drawX + ball.getRadius();
        int y1 = drawY + ball.getRadius();
        Vector2d speed = ball.getSpeed().scale(4);
        int x2 = (int) (x1 + speed.getX());
        int y2 = (int) (y1 + speed.getY());

        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawLine(x1,y1,x2,y2);

        // Reset stroke and color to default
        graphics.setStroke(defaultStroke);
        graphics.setColor(defaultColor);
    }

}
