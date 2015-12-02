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

    public static Vector2d fromAngleRad(double angleRad, double magnitude) {
        Vector2d v = new Vector2d(Math.cos(angleRad), Math.sin(angleRad));
        return v.setMagnitude(magnitude);
    }
    public static Vector2d fromAngleDeg(double angleDeg, double magnitude) {
        return fromAngleRad(Math.toRadians(angleDeg), magnitude);
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

    public double getAngleRad() {
        return Math.atan2(y, x);
    }
    public double getAngleDeg() {
        return Math.toDegrees(getAngleRad());
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

    /**
     * Calculates closest point p on line defined by v and w.
     * Projects p on line v and w. If the projection is between v and w this point will be returned.
     * If the projection is outside of the line boundaries the closest point v or w are returned.
     * @param p The point to project on the line.
     * @param v Start of the line.
     * @param w End of the line.
     * @return Closest point p on line defined by v and w.
     */
    public static Vector2d getClosestPointOnLine(Vector2d p, Vector2d v, Vector2d w) {
        double l2 = Math.pow(v.sub(w).magnitude(), 2);
        if (l2 == 0)
            return v;
        else {
            double t = p.sub(v).dot(w.sub(v)) / l2;
            if (t < 0)
                return v;
            else if (t > 1)
                return w;
            else {
                Vector2d projection = v.add(w.sub(v).scale(t));
                return projection;
            }
        }

    }
}
