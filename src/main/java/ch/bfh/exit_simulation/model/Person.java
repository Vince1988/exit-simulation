package ch.bfh.exit_simulation.model;

import ch.bfh.exit_simulation.GamePanel;
import ch.bfh.exit_simulation.SimulationCanvas;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Genecand on 21.09.2015.
 */
public class Person {

    private final int radius;
    private final double maxAcceleration;
    private final double maxSpeed;
    private final Color color;

    private Vector2d lastPos;
    private Vector2d currentPos;
    private Vector2d speed;

    private final double kg;


    public Person(float x, float y, int radius, Color color) {
        this.color = color;
        this.radius = radius;
        this.maxAcceleration = radius/20d;
        this.maxSpeed = radius;

        this.kg = this.radius;

        this.speed = Vector2d.ZERO;
        this.currentPos = new Vector2d(x, y);
        this.lastPos = this.currentPos.copy();
    }

    public Person(float x, float y, int radius, float speed, Color color) {
        this(x, y, radius, color);
    }
    public Person(float x, float y, int radius, float xs, float ys, Color color) {
        this(x, y, radius, color);
    }

    public static Person createRandomPerson() {
        float x = (float) (Math.random() * SimulationCanvas.W);
        float y = (float) (Math.random() * SimulationCanvas.H);
        int radius = (int) (Math.random() * 10) + 5;
        float speed = (float) (Math.random() * 10) + 5;
        Color c = new Color((int) (Math.random()  * 255),(int) (Math.random()  * 255),(int) (Math.random()  * 255));

        return new Person(x,y,radius,speed,c);
    }

    public static Person createGenericPerson(int i) {
//        float x = SimulationCanvas.W / 2;
//        float y = SimulationCanvas.H / 2;
        int radius = 15;
        float speed = 5;
        Color c = Color.BLACK;

        float x = radius + i * radius * 2;
        float y = x;

        return new Person(x,y,radius,speed,c);
    }

    public static List<Person> createCardinalPersons() {
        int h = SimulationCanvas.H / 2;
        int w = SimulationCanvas.W / 2;
        int r = 10;
        float speed = 10;

        List<Person> persons = new ArrayList<>();
        persons.add(new Person(r, h, r, speed, 0, Color.BLACK));
        persons.add(new Person(SimulationCanvas.W - r, h, r, -speed, 0, Color.BLACK));
        persons.add(new Person(w, r, r, 0, speed, Color.BLACK));
        persons.add(new Person(w, SimulationCanvas.H - r, r, 0, -speed, Color.BLACK));
        persons.add(new Person(2*r, 2*r, r, speed, speed, Color.BLACK));

        return persons;
    }

    public static List<Person> placeRandomPersons(int amount, GamePanel panel) {
        int radius = Integer.parseInt(panel.getProps().getProperty("personRadius"));

        ArrayList<Person> persons = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            Person p = new Person(0, 0, radius, Color.BLACK);
            p.placeRandomOnScene(panel);
            persons.add(p);
        }
        return persons;
    }

    public void placeRandomOnScene(GamePanel panel) {
        Vector2d pos;
        do {
            // pick random spot on the scene
            pos = new Vector2d(Math.random() * SimulationCanvas.W, Math.random() * SimulationCanvas.H);

            // use the path finder to check if the person is at an inaccessible place (no path to exit).
        } while (panel.getPathFinder().getPathToExit(pos) == null);

        // set the found position
        setCurrentPos(pos);
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

    public double getMaxAcceleration() { return maxAcceleration; };

    public double getMaxSpeed() { return maxSpeed; };
}

