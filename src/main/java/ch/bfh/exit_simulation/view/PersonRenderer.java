package ch.bfh.exit_simulation.view;

import ch.bfh.exit_simulation.GamePanel;
import ch.bfh.exit_simulation.controller.PersonController;
import ch.bfh.exit_simulation.model.Person;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

/**
 * Created by Vincent Genecand on 05.10.2015.
 */
public class PersonRenderer implements Renderer {

    private final Person person;

    public PersonRenderer(Person person) {
        this.person = person;
    }

    @Override
    public void render(Graphics2D graphics, float interpolation) {
        GamePanel panel = GamePanel.getInstance();

        // Save currently used stroke and color, to be able to reset them at the end.
        Color defaultColor = graphics.getColor();
        Stroke defaultStroke = graphics.getStroke();

        // draw the person
        graphics.setColor(person.getColor());
        int drawX = (int) ((person.getCurrentPos().getX() - person.getLastPos().getX()) * interpolation + person.getLastPos().getX());
        int drawY = (int) ((person.getCurrentPos().getY() - person.getLastPos().getY()) * interpolation + person.getLastPos().getY());

        // draw the slow down ranges
        if (Boolean.parseBoolean(panel.props.getProperty("renderApproachRanges"))) {
            PersonController bc = new PersonController(person, panel);
            Vector2d sc = bc.getMaxSpeedCalcPoint();
            double closestObject = panel.getClosestEntityDistance(sc, person.getCurrentPos());

            if (closestObject < PersonController.CRAWL_DISTANCE * person.getRadius()) {
                graphics.setColor(new Color(1f, 0f, 0f, 0.4f));
                int crawl_radius = (int) (person.getRadius() * (PersonController.CRAWL_DISTANCE + 1));
                graphics.fillOval((int) sc.getX() - crawl_radius, (int) sc.getY() - crawl_radius, 2 * crawl_radius, 2 * crawl_radius);
            } else if (closestObject < PersonController.CAREFUL_DISTANCE * person.getRadius()) {
                graphics.setColor(new Color(1f, 0f, 0f, 0.1f));
                int careful_radius = (int) (person.getRadius() * (PersonController.CAREFUL_DISTANCE + 1));
                graphics.fillOval((int) sc.getX() - careful_radius, (int) sc.getY() - careful_radius, 2 * careful_radius, 2 * careful_radius);
            }
        }

        // draw navigation direction
        Vector2d direction = panel.getNavigator().getDirection(person.getCurrentPos());
        if (Boolean.parseBoolean(panel.props.getProperty("renderNavigationDirection"))) {
            Vector2d directionAbs = person.getCurrentPos().add(direction.scale(person.getRadius() * 3));
            graphics.setColor(Renderer.getColorFromName(panel.props.getProperty("navigationDirectionColor")));
            graphics.draw(new Line2D.Double(person.getCurrentPos().getPoint(), directionAbs.getPoint()));
        }

        // draw speed vector
        if (Boolean.parseBoolean(panel.props.getProperty("renderMomentumDirection"))) {
            Vector2d speed = person.getSpeed().normalize().scale(person.getRadius()).scale(person.getSpeed().magnitude()/ person.getMaxSpeed() + 1);
            // length is proportional to speed
            Vector2d speedAbs = person.getCurrentPos().add(speed);
            graphics.setColor(Renderer.getColorFromName(panel.props.getProperty("momentumDirectionColor")));
            graphics.draw(new Line2D.Double(person.getCurrentPos().getPoint(), speedAbs.getPoint()));
        }

        graphics.setColor(defaultColor);
        graphics.fillOval(drawX- person.getRadius(), drawY- person.getRadius(), person.getRadius() * 2, person.getRadius() * 2);

//        this.renderSpeedVector(drawX, drawY, graphics);

        // Reset stroke and color to default
        graphics.setStroke(defaultStroke);
        graphics.setColor(defaultColor);
    }

    private void renderSpeedVector(int drawX, int drawY, Graphics2D graphics) {
        // Create a line showing the direction and speed (length) of the person
        int x1 = drawX + person.getRadius();
        int y1 = drawY + person.getRadius();
        Vector2d speed = person.getSpeed().scale(4);
        int x2 = (int) (x1 + speed.getX());
        int y2 = (int) (y1 + speed.getY());

        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawLine(x1, y1, x2, y2);
    }

}
