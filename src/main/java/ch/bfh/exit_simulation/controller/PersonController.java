package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.GamePanel;
import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.model.Person;
import ch.bfh.exit_simulation.util.Vector2d;

/**
 * Created by Vincent Genecand on 05.10.2015.
 */
public class PersonController implements Controller {

    // If the person is closer than the x amount of radiuses then it will slow down to the described speed.
    public static final double CRAWL_DISTANCE = 2;
    public static final double CAREFUL_DISTANCE = 6;

    private GamePanel panel;
    private final Person person;

    public PersonController(Person _person, GamePanel _panel) {
        this.person = _person;
        this.panel = _panel;
    }

    @Override
    public void update() {
        // apply new position
        person.setLastPos(person.getCurrentPos());

        Vector2d direction = panel.getNavigator().getDirection(person.getCurrentPos());
        Vector2d targetSpeed = direction.scale(getMaxSpeed());
        Vector2d speedDiff = targetSpeed.sub(person.getSpeed());
        Vector2d cappedSpeedDiff = speedDiff.setMaxMagnitude(person.getMaxAcceleration());
        Vector2d newSpeed = person.getSpeed().add(cappedSpeedDiff);
        person.setSpeed(newSpeed);

        person.setCurrentPos(person.getCurrentPos().add(person.getSpeed()));

        // Bounce at window edges
        if (person.getCurrentPos().getX() + person.getRadius() >= SimulationCanvas.W) {
            person.setSpeed(person.getSpeed().reflect(new Vector2d(0, 1)));
            person.setCurrentPos(new Vector2d(SimulationCanvas.W - person.getRadius(), person.getCurrentPos().getY()));
        } else if (person.getCurrentPos().getX() - person.getRadius() <= 0) {
            person.setSpeed(person.getSpeed().reflect(new Vector2d(0, 1)));
            person.setCurrentPos(new Vector2d(person.getRadius(), person.getCurrentPos().getY()));
        }

        if (person.getCurrentPos().getY() + person.getRadius() >= SimulationCanvas.H) {
            person.setSpeed(person.getSpeed().reflect(new Vector2d(1, 0)));
            person.setCurrentPos(new Vector2d(person.getCurrentPos().getX(), SimulationCanvas.H - person.getRadius()));
        } else if (person.getCurrentPos().getY() - person.getRadius() <= 0) {
            person.setSpeed(person.getSpeed().reflect(new Vector2d(1, 0)));
            person.setCurrentPos(new Vector2d(person.getCurrentPos().getX(), person.getRadius()));
        }
    }

    /**
     * The maximum speed is dependent on the environment. The closer the entity is to another entity
     * or wall, the slower it gets. You wouldn't want to risk a crash.
     * @return The max speed depending on the environment.
     */
    public double getMaxSpeed() {
        Vector2d calcCenter = getMaxSpeedCalcPoint();

        double speedModifier = 1.0;
        double closestObject = panel.getClosestEntityDistance(calcCenter, person.getCurrentPos());
        if (closestObject < CRAWL_DISTANCE * person.getRadius())
            speedModifier = 0.2;
        else if (closestObject < CAREFUL_DISTANCE * person.getRadius())
            speedModifier = 0.5;
        else
            speedModifier = 1.0;
        return person.getMaxSpeed()*speedModifier;
    }

    public Vector2d getMaxSpeedCalcPoint() {
        return person.getCurrentPos().add(person.getSpeed().normalize().scale(person.getRadius()* PersonController.CRAWL_DISTANCE));
    }

    public void elasticCollision(Person b) {
        Person a = this.person;

        //TODO: Improve! code duplication...
        Vector2d distance = a.getCurrentPos().sub(b.getCurrentPos());

        if (distance.magnitude() <= b.getRadius() + a.getRadius()) {
            Vector2d relativeSpeed = b.getSpeed().sub(a.getSpeed());
            double dot = distance.dot(relativeSpeed);

            if (dot > 0) {

                Vector2d n = a.getCurrentPos().sub(b.getCurrentPos());
                Vector2d un = n.normalize();
                Vector2d ut = new Vector2d(-un.getY(), un.getX());

                Vector2d v1 = a.getSpeed();
                Vector2d v2 = b.getSpeed();

                double v1t = ut.dot(v1);
                double v2t = ut.dot(v2);
                double v1n = un.dot(v1);
                double v2n = un.dot(v2);

                double v1n_new = (v1n * (a.getMass() - b.getMass()) + 2 * b.getMass() * v2n) / (a.getMass() + b.getMass());
                double v2n_new = (v2n * (b.getMass() - a.getMass()) + 2 * a.getMass() * v1n) / (a.getMass() + b.getMass());

                Vector2d v1n_vec = un.scale(v1n_new);
                Vector2d v2n_vec = un.scale(v2n_new);

                Vector2d v1t_vec = ut.scale(v1t);
                Vector2d v2t_vec = ut.scale(v2t);

                Vector2d v1_new = v1n_vec.add(v1t_vec);
                Vector2d v2_new = v2n_vec.add(v2t_vec);

                a.setSpeed(v1_new);
                b.setSpeed(v2_new);
            }
        }
    }
}
