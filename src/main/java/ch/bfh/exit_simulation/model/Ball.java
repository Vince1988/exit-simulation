package ch.bfh.exit_simulation.model;

import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.*;

/**
 * Created by Vincent Genecand on 21.09.2015.
 */
public class Ball {

    private final int radius;

    private float lastX;
    private float lastY;

    private Vector2d lastPos;
    private Vector2d currentPos;
    private Vector2d speed;

    private float currentX;
    private float currentY;

    private float speedX;
    private float speedY;

    private final Color color;

    public Ball(float x, float y, int radius, float speed, Color color) {
        this.color = color;
        currentX = lastX = x;
        currentY = lastY = y;
        this.radius = radius;

        speedX = (float) Math.random() * speed * 2 - speed;
        speedY = (float) Math.random() * speed * 2 - speed;

        this.currentPos = new Vector2d(x, y);
        this.lastPos = this.currentPos.copy();
    }

    public static Ball createRandomBall() {
        float x = (float) (Math.random() * SimulationCanvas.W);
        float y = (float) (Math.random() * SimulationCanvas.H);
        int radius = (int) (Math.random() * 20) + 10;
        float speed = (float) (Math.random() * 10) + 5;
        Color c = new Color((int) (Math.random()  * 255),(int) (Math.random()  * 255),(int) (Math.random()  * 255));

        return new Ball(x,y,radius,speed,c);
    }

    public int getRadius() {
        return radius;
    }

    public float getLastX() {
        return lastX;
    }

    public float getLastY() {
        return lastY;
    }

    public float getCurrentX() {
        return currentX;
    }

    public float getCurrentY() {
        return currentY;
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedY() {
        return speedY;
    }

    public Color getColor() {
        return color;
    }

    public void setLastX(float lastX) {
        this.lastX = lastX;
    }

    public void setLastY(float lastY) {
        this.lastY = lastY;
    }

    public void setCurrentX(float currentX) {
        this.currentX = currentX;
    }

    public void setCurrentY(float currentY) {
        this.currentY = currentY;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }
}
