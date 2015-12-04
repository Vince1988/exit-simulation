package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.GamePanel;
import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.util.Vector2d;
import ch.bfh.exit_simulation.view.Renderer;

/**
 * Created by Vincent Genecand on 05.10.2015.
 */
public class BallController extends Controller<Ball> {

    // If the ball is closer than the x amount of radiuses then it will slow down to the described speed.
    public static final double CRAWL_DISTANCE = 2;
    public static final double CAREFUL_DISTANCE = 6;

    private static GamePanel panel = GamePanel.getInstance();

    public BallController(Ball model, Renderer<Ball> view) {
        super(model, view);
    }

    @Override
    public void updateModel() {
        // apply new position
        this.model.setLastPos(this.model.getCurrentPos());

        Vector2d direction = panel.getNavigator().getDirection(this.model.getCurrentPos());
        Vector2d targetSpeed = direction.scale(getMaxSpeed());
        Vector2d speedDiff = targetSpeed.sub(this.model.getSpeed());
        Vector2d cappedSpeedDiff = speedDiff.setMaxMagnitude(this.model.getMaxAcceleration());
        Vector2d newSpeed = this.model.getSpeed().add(cappedSpeedDiff);
        this.model.setSpeed(newSpeed);

        this.model.setCurrentPos(this.model.getCurrentPos().add(this.model.getSpeed()));

        // Bounce at window edges
        if (this.model.getCurrentPos().getX() + this.model.getRadius() >= SimulationCanvas.W) {
            this.model.setSpeed(this.model.getSpeed().reflect(new Vector2d(0, 1)));
            this.model.setCurrentPos(new Vector2d(SimulationCanvas.W - this.model.getRadius(), this.model.getCurrentPos().getY()));
        } else if (this.model.getCurrentPos().getX() - this.model.getRadius() <= 0) {
            this.model.setSpeed(this.model.getSpeed().reflect(new Vector2d(0, 1)));
            this.model.setCurrentPos(new Vector2d(this.model.getRadius(), this.model.getCurrentPos().getY()));
        }

        if (this.model.getCurrentPos().getY() + this.model.getRadius() >= SimulationCanvas.H) {
            this.model.setSpeed(this.model.getSpeed().reflect(new Vector2d(1, 0)));
            this.model.setCurrentPos(new Vector2d(this.model.getCurrentPos().getX(), SimulationCanvas.H - this.model.getRadius()));
        } else if (this.model.getCurrentPos().getY() - this.model.getRadius() <= 0) {
            this.model.setSpeed(this.model.getSpeed().reflect(new Vector2d(1, 0)));
            this.model.setCurrentPos(new Vector2d(this.model.getCurrentPos().getX(), this.model.getRadius()));
        }
    }

    /**
     * The maximum speed is dependent on the environment. The closer the entity is to another entity
     * or wall, the slower it gets. You wouldn't want to risk a crash.
     *
     * @return The max speed depending on the environment.
     */
    public double getMaxSpeed() {
        Vector2d calcCenter = getMaxSpeedCalcPoint();

        double speedModifier = 1.0;
        double closestObject = panel.getClosestEntityDistance(calcCenter, this.model.getCurrentPos());
        if (closestObject < CRAWL_DISTANCE * this.model.getRadius())
            speedModifier = 0.2;
        else if (closestObject < CAREFUL_DISTANCE * this.model.getRadius())
            speedModifier = 0.5;
        else
            speedModifier = 1.0;
        return this.model.getMaxSpeed() * speedModifier;
    }

    public Vector2d getMaxSpeedCalcPoint() {
        return this.model.getCurrentPos().add(this.model.getSpeed().normalize().scale(this.model.getRadius() * BallController.CRAWL_DISTANCE));
    }

    public void elasticCollision(Ball b) {
        Ball a = this.model;

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
