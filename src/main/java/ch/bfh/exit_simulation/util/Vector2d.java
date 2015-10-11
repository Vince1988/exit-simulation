package ch.bfh.exit_simulation.util;

import java.util.Objects;

/**
 * Created by Vincent Genecand on 07.10.2015.
 */
public class Vector2d {

    private final double x;
    private final double y;

    public Vector2d() {
        this(0, 0);
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2d(Vector2d v) {
        this(v.x, v.y);
    }

    /**
     * Calculates the magnitude (length) of the vector.
     *
     * @return Magnitude (length) of the vector.
     */
    public double magnitude() {
        double x2 = Math.pow(this.getX(), 2);
        double y2 = Math.pow(this.getY(), 2);

        return Math.sqrt(x2 + y2);
    }

    public Vector2d scale(double scalar) {
        return new Vector2d(scalar * this.getX(), scalar * this.getY());
    }

    public Vector2d negate() {
        return this.scale(-1);
    }

    public Vector2d add(Vector2d v) {
        return new Vector2d(this.getX() + v.getX(), this.getY() + v.getY());
    }

    public Vector2d sub(Vector2d v) {
        return this.add(v.negate());
    }

    public double dot(Vector2d v) {
        return (this.getX() * v.getX()) + (this.getY() * v.getY());
    }

    public Vector2d copy() {
        return new Vector2d(this);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return this.copy();
    }

    @Override
    public String toString() {
        return "Vector2d[x=" + this.getX() + ", y=" + this.getY() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        Vector2d that = (Vector2d) o;
        boolean equal = true;

        equal &= Objects.equals(this.getX(), that.getX());
        equal &= Objects.equals(this.getY(), that.getY());

        return equal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }
}
