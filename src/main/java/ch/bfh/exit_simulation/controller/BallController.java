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

    public Collision highSpeedCollision(Ball b2) {
        Collision c = null;
        if (this.ball != b2) {
            Ball a = this.ball;
            Ball b = b2;
            double maxT = a.getCurrentPos().sub(a.getLastPos()).magnitude() / a.getSpeed().magnitude();

            Vector2d pAB = a.getLastPos().sub(b.getLastPos());
            Vector2d vAB = a.getSpeed().sub(b.getSpeed());
            double r = Math.pow(a.getRadius() + b.getRadius(), 2);

            double mfA = vAB.dot(vAB);
            double mfB = 2 * pAB.dot(vAB);
            double mfC = pAB.dot(pAB) - r;

            double determinant = Math.pow(mfB, 2) - (4 * mfA * mfC);

            if (determinant > 0) {
                double t0 = (-mfB - Math.sqrt(determinant)) / (2 * mfA);
                double t1 = (-mfB + Math.sqrt(determinant)) / (2 * mfA);
                double t = Math.min(t0, t1);

                if (t >= 0 && t <= maxT) {
                    c = new Collision(a, b, t, maxT);
                }
            }
        }

        return c;
    }
}
