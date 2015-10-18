package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.util.Vector2d;

import java.util.Set;

/**
 * Created by Vincent Genecand on 17.10.2015.
 */
public class CollisionAnalyzer {

    private CollisionAnalyzer() {
        throw new AssertionError("Should not be initialized");
    }

    public static void executeValidCollisions(Set<Collision> collisions) {
        collisions.stream()
                .filter(c -> lowestTime(c.getA(), collisions) == c.getT() && lowestTime(c.getB(), collisions) == c.getT())
                .forEach(CollisionAnalyzer::executeCollision);
    }

    private static double lowestTime(Ball b, Set<Collision> collisions) {
        double t = Double.MAX_VALUE;

        for (Collision c : collisions) {
            if (c.contains(b) && c.getT() < t) {
                t = c.getT();
            }
        }

        return t;
    }

    private static void executeCollision(Collision c) {
        Ball a = c.getA();
        Ball b = c.getB();
        double t = c.getT();
        double maxT = c.getMaxT();

        Vector2d aPosition = a.getLastPos().add(a.getSpeed().scale(t));
        Vector2d bPosition = b.getLastPos().add(b.getSpeed().scale(t));

        Vector2d n = aPosition.sub(bPosition);
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


//        a.setLastPos(aPosition);
//        b.setLastPos(bPosition);
        a.setCurrentPos(aPosition.add(a.getSpeed().scale(maxT - t)));
        b.setCurrentPos(bPosition.add(b.getSpeed().scale(maxT - t)));
    }
}
