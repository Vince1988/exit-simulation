package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.GamePanel;
import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.util.Vector2d;

/**
 * Created by Vincent Genecand on 05.10.2015.
 */
public class BallController implements Controller {

    // If the ball is closer than the x amount of radiuses then it will slow down to the described speed.
    public static final double CRAWL_DISTANCE = 2;
    public static final double CAREFUL_DISTANCE = 6;

    private GamePanel panel;
    private final Ball ball;

    public BallController(Ball _ball, GamePanel _panel) {
        this.ball = _ball;
        this.panel = _panel;
    }

    @Override
    public void update() {
        // apply new position
        ball.setLastPos(ball.getCurrentPos());

        Vector2d direction = panel.getNavigator().getDirection(ball.getCurrentPos());
        Vector2d targetSpeed = direction.scale(getMaxSpeed());
        Vector2d speedDiff = targetSpeed.sub(ball.getSpeed());
        Vector2d cappedSpeedDiff = speedDiff.setMaxMagnitude(ball.getMaxAcceleration());
        Vector2d newSpeed = ball.getSpeed().add(cappedSpeedDiff);
        ball.setSpeed(newSpeed);

        ball.setCurrentPos(ball.getCurrentPos().add(ball.getSpeed()));

        // Bounce at window edges
        if (ball.getCurrentPos().getX() + ball.getRadius() >= SimulationCanvas.W) {
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

    /**
     * The maximum speed is dependent on the environment. The closer the entity is to another entity
     * or wall, the slower it gets. You wouldn't want to risk a crash.
     * @return The max speed depending on the environment.
     */
    public double getMaxSpeed() {
        Vector2d calcCenter = getMaxSpeedCalcPoint();

        double speedModifier = 1.0;
        double closestObject = panel.getClosestEntityDistance(calcCenter, ball.getCurrentPos());
        if (closestObject < CRAWL_DISTANCE * ball.getRadius())
            speedModifier = 0.2;
        else if (closestObject < CAREFUL_DISTANCE * ball.getRadius())
            speedModifier = 0.5;
        else
            speedModifier = 1.0;
        return ball.getMaxSpeed()*speedModifier;
    }

    public Vector2d getMaxSpeedCalcPoint() {
        return ball.getCurrentPos().add(ball.getSpeed().normalize().scale(ball.getRadius()*BallController.CRAWL_DISTANCE));
    }

    public void elasticCollision(Ball b) {
        Ball a = this.ball;

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
