package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.model.Ball;

/**
 * Created by Vincent Genecand on 17.10.2015.
 */
public class Collision {

    private final Ball a;
    private final Ball b;
    private final double t;
    private final double maxT;

    public Collision(Ball a, Ball b, double t, double maxT) {
        this.a = a;
        this.b = b;
        this.t = t;
        this.maxT = maxT;
    }

    public Ball getA() {
        return a;
    }

    public Ball getB() {
        return b;
    }

    public double getT() {
        return t;
    }

    public double getMaxT() {
        return maxT;
    }

    public boolean contains(Ball ball) {
        return a.equals(ball) || b.equals(ball);
    }
}
