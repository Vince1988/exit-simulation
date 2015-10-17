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

    private final double kg;


    public Ball(float x, float y, int radius, float xs, float ys, Color color) {
        this.color = color;
        this.radius = radius;

        this.kg = this.radius;

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

    public double getMass() {
        return this.getKg() / (Math.PI * Math.pow(this.getRadius(), 2));
    }

    public double getKg() {
        return kg;
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
}

