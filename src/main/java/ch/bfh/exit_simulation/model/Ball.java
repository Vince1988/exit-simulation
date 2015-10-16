package ch.bfh.exit_simulation.model;

import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Genecand on 21.09.2015.
 */
public class Ball {

    private final int radius;
    private final Color color;

    private Vector2d lastPos;
    private Vector2d currentPos;
    private Vector2d speed;

    public Ball(float x, float y, int radius, float xs, float ys, Color color) {
        this.color = color;
        this.radius = radius;

        this.speed = new Vector2d(xs, ys);
        this.currentPos = new Vector2d(x, y);
        this.lastPos = this.currentPos.copy();
    }

    public Ball(float x, float y, int radius, float speed, Color color) {
        this(x, y, radius, (float) Math.random() * speed * 2 - speed, (float) Math.random() * speed * 2 - speed, color);
    }

    public static Ball createRandomBall() {
        float x = (float) (Math.random() * SimulationCanvas.W);
        float y = (float) (Math.random() * SimulationCanvas.H);
        int radius = (int) (Math.random() * 10) + 5;
        float speed = (float) (Math.random() * 10) + 5;
        Color c = new Color((int) (Math.random()  * 255),(int) (Math.random()  * 255),(int) (Math.random()  * 255));

        return new Ball(x,y,radius,speed,c);
    }

    public static Ball createGenericBall(int i) {
//        float x = SimulationCanvas.W / 2;
//        float y = SimulationCanvas.H / 2;
        int radius = 15;
        float speed = 5;
        Color c = Color.BLACK;

        float x = radius + i * radius * 2;
        float y = x;

        return new Ball(x,y,radius,speed,c);
    }

    public static List<Ball> createCardinalBalls() {
        int h = SimulationCanvas.H / 2;
        int w = SimulationCanvas.W / 2;
        int r = 50;
        float speed = 10;

        List<Ball> balls = new ArrayList<>();
        balls.add(new Ball(r, h, r, speed, 0, Color.BLACK));
        balls.add(new Ball(SimulationCanvas.W - r, h, r, -speed, 0, Color.BLACK));
        balls.add(new Ball(w, r, r, 0, speed, Color.BLACK));
        balls.add(new Ball(w, SimulationCanvas.H - r, r, 0, -speed, Color.BLACK));

        return balls;
    }

    public int getRadius() {
        return radius;
    }

    public Color getColor() {
        return color;
    }

    public Vector2d getLastPos() {
        return lastPos;
    }

    public void setLastPos(Vector2d lastPos) {
        this.lastPos = lastPos;
    }

    public Vector2d getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(Vector2d currentPos) {
        this.currentPos = currentPos;
    }

    public Vector2d getSpeed() {
        return speed;
    }

    public void setSpeed(Vector2d speed) {
        this.speed = speed;
    }

    public void elasticCollision(Ball b2) {
        double d = this.currentPos.sub(b2.currentPos).magnitude();
        if (d <= b2.getRadius() + this.getRadius()) {


            Vector2d n = this.currentPos.sub(b2.currentPos);
            Vector2d un = n.normalize();
            Vector2d ut = new Vector2d(-un.getY(), un.getX());

            Vector2d v1 = this.getSpeed();
            Vector2d v2 = b2.getSpeed();

            double v1t = ut.dot(v1);
            double v2t = ut.dot(v2);
            double v1n = un.dot(v1);
            double v2n = un.dot(v2);

            double v1n_new = (v1n * (this.getRadius() - b2.getRadius()) + 2 * b2.getRadius() * v2n) / (this.getRadius() + b2.getRadius());
            double v2n_new = (v2n * (b2.getRadius() - this.getRadius()) + 2 * this.getRadius() * v1n) / (this.getRadius() + b2.getRadius());

            Vector2d v1n_vec = un.scale(v1n_new);
            Vector2d v2n_vec = un.scale(v2n_new);

            Vector2d v1t_vec = ut.scale(v1t);
            Vector2d v2t_vec = ut.scale(v2t);

            Vector2d v1_new = v1n_vec.add(v1t_vec);
            Vector2d v2_new = v2n_vec.add(v2t_vec);

            this.setSpeed(v1_new);
            b2.setSpeed(v2_new);
        }
    }
}

