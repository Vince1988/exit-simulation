package ch.bfh.exit_simulation.util;

import com.sun.javafx.geom.Vec2d;

/**
 * Created by Vincent Genecand on 07.10.2015.
 */
public class Vector2d {

    private final double x;
    private final double y;

    public Vector2d() {
        this.x = 0;
        this.y = 0;
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d add(Vector2d v) {
        return new Vector2d(this.getX() + v.getX(), this.getY() + v.getY());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this.copy();
    }

    public Vector2d copy() {
        return new Vector2d(getX(), getY());
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
