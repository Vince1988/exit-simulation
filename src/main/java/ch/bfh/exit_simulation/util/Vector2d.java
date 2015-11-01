package ch.bfh.exit_simulation.util;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.util.Objects;

/**
 * Created by Vincent Genecand on 07.10.2015.
 */
public class Vector2d {

    public static final Vector2d ZERO = new Vector2d();
    public static final Vector2d MAX = new Vector2d(Double.MAX_VALUE, Double.MAX_VALUE);

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

    public Vector2d(Point2D p) {this(p.getX(), p.getY());}

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

    public Vector2d div(double scalar) {
        return new Vector2d(this.getX() / scalar, this.getY() / scalar);
    }

    public double dot(Vector2d v) {
        return (this.getX() * v.getX()) + (this.getY() * v.getY());
    }

    public Vector2d normalize() {
        double m = this.magnitude();
        return m != 0 ? this.div(m) : ZERO;
    }

    public Vector2d reflect(Vector2d v) {
        Vector2d n = v.normalize();
        double dot = this.dot(n);

        return n.scale(2 * dot).sub(this);
    }

    public Vector2d setMagnitude(double length) {
        Vector2d unit = this.normalize();
        return unit.scale(length);
    }
    public Vector2d setMaxMagnitude(double maxLength) {
        double length = this.magnitude();
        return (length < maxLength) ? this.copy() : this.setMagnitude(maxLength);
    }

    /**
     * Euclidean distance between (location) vectors
     * @param v other vector to calc distance to
     * @return distance between vectors
     */
    public double distance(Vector2d v) {
        return Math.sqrt(Math.pow(this.getX()-v.getX(),2) + Math.pow(this.getY()-v.getY(),2));
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

    public Point getPoint() {
        return new Point(new Double(this.getX()).intValue(), new Double(this.getY()).intValue());
    }
}
