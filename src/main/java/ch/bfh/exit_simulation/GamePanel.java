package ch.bfh.exit_simulation;

import ch.bfh.exit_simulation.controller.*;
import ch.bfh.exit_simulation.model.*;
import ch.bfh.exit_simulation.util.SceneLoader;
import ch.bfh.exit_simulation.model.Exit;
import ch.bfh.exit_simulation.model.IObstacle;
import ch.bfh.exit_simulation.model.ObstaclePoly;
import ch.bfh.exit_simulation.util.Vector2d;
import ch.bfh.exit_simulation.view.ObstaclePolyRenderer;
import ch.bfh.exit_simulation.view.PersonRenderer;
import ch.bfh.exit_simulation.view.Renderer;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Vincent Genecand on 21.09.2015.
 */
public class GamePanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private Set<Person> persons;
    private final List<Vector2d> spawnPoints = new ArrayList<>();
    public List<IObstacle> obstacles;
    public Exit exit;
    private List<Line2D> obstacleNavLineCache;

    private IPathFinder pathfinder;
    private INavigator navigator;
    private AttractionNavigator attractionNavigator;

    private static GamePanel _instance;
    public static GamePanel getInstance() {
        if (_instance == null)
            _instance = new GamePanel();
        return _instance;
    }
    private GamePanel() {
        this.persons = new HashSet<>();
        this.obstacles = new ArrayList<>();

        loadScene();

        //this.obstacles.addAll(ObstaclePoly.createHallway());
        this.obstacles.addAll(ObstacleBoundarie.getGameBoundaries());

        this.pathfinder = loadPathFinder();
        this.navigator = loadNavigator();
        this.attractionNavigator = new AttractionNavigator();

        int person_count = Integer.parseInt(props.getProperty("personCount"));
        this.persons.addAll(Person.placeRandomPersons(person_count, this));
        this.spawnPoints.addAll(this.persons.stream().map(Person::getCurrentPos).collect(Collectors.toList()));
    }

    public void update() {
        this.persons.forEach(person -> new PersonController(person, this).update());
        List<Person> personsToCheck = new ArrayList<>(this.persons);

        for (Person p : this.persons) {
            personsToCheck.remove(p);

            // check for exit collision
            if (exit.getDistance(p.getCurrentPos()) < p.getRadius()) {
                spawnPoints.add(p.placeRandomOnScene(this));
            }

            PersonController pc = new PersonController(p, this);
            personsToCheck.forEach(pc::elasticCollision);
            for (IObstacle obst: obstacles) {
                if (obst instanceof ObstaclePoly) {
                    pc.collision((ObstaclePoly) obst);
                }
            }


        }


    }

    public void render(Graphics2D g, float interpolation) {
        this.persons.forEach(person -> new PersonRenderer(person).render(g, interpolation));
        for (IObstacle obst: obstacles) {
            if (obst instanceof ObstaclePoly) new ObstaclePolyRenderer((ObstaclePoly)obst).render(g, interpolation);
        }

        if (Boolean.parseBoolean(props.getProperty("renderNavigationLines"))) {
            g.setColor(Renderer.getColorFromName(props.getProperty("navigationLineColor")));
            getObstacleNavigationLines().forEach(g::draw);
        }

        g.setColor(Color.BLUE);
        g.fill(this.exit);

        for (Person b: this.persons) {
            if (Boolean.parseBoolean(props.getProperty("renderPath"))) {
                g.setColor(Renderer.getColorFromName(props.getProperty("exitPathColor")));
                List<Vector2d> path = pathfinder.getPathToExit(b.getCurrentPos());
                if (path == null) continue;
                for (int i = 0; i < path.size() - 1; i++) {
                    g.draw(new Line2D.Double(path.get(i).getPoint(), path.get(i + 1).getPoint()));
                }
            }
            if (Boolean.parseBoolean(props.getProperty("renderLineToClosestObject"))) {
                Vector2d closestPointOnObst = getClosestEntity(b.getCurrentPos());
                g.setColor(Renderer.getColorFromName(props.getProperty("lineToClosestObjectColor")));
                g.draw(new Line2D.Double(b.getCurrentPos().getPoint(), closestPointOnObst.getPoint()));
            }
        }

        if (Boolean.parseBoolean(props.getProperty("renderSpawnPoints", "false"))) {
            for (Vector2d placement : spawnPoints) {
                g.drawRect((int) placement.getX(), (int) placement.getY(), 1, 1);
            }
        }
    }

    public List<Line2D> getObstacleNavigationLines() {
        if (obstacleNavLineCache != null)
            return obstacleNavLineCache;

        ArrayList<Line2D> lst = new ArrayList<>();
        ArrayList<Vector2d> navPoints = new ArrayList<>();
        this.obstacles.forEach(obstacle -> navPoints.addAll(obstacle.getNavigationPoints()));

        for (int i = 0; i < navPoints.size(); i++) {
            for (int j = i + 1; j < navPoints.size(); j++) {
                lst.add(new Line2D.Double(navPoints.get(i).getPoint(), navPoints.get(j).getPoint()));
            }
        }
        ArrayList<Line2D> collFreeList = new ArrayList<>();
        for (Line2D line : lst) {
            boolean collides = false;
            for (IObstacle obst : this.obstacles) {
                if (obst.collides(line)) {
                    collides = true;
                    break;
                }
            }
            if (!collides)
                collFreeList.add(line);
        }
        obstacleNavLineCache = collFreeList;
        return collFreeList;
    }

    /**
     * Get closest point to an obstacle.
     * @param start Current position to search from.
     * @return The closest point to the start on an obstacle.
     */
    public Vector2d getClosestObstaclePoint(Vector2d start) {
        Vector2d closestPoint = Vector2d.MAX;
        for (IObstacle obstacle: this.obstacles) {

            Vector2d pointOnObst = obstacle.getClosestPoint(start);
            if (start.distance(closestPoint) > start.distance(pointOnObst))
                closestPoint = pointOnObst;
        }
        return closestPoint;
    }

    public Vector2d getClosestEntity(Vector2d start) {
        return getClosestEntity(start, null);
    }
    public Vector2d getClosestEntity(Vector2d start, Vector2d ignoreSelf) {
        Vector2d closestPoint = getClosestObstaclePoint(start);
        for (Person person : persons) {
            if (person.getCurrentPos().equals(ignoreSelf)) continue;
            if (person.getCurrentPos().equals(start)) continue;
            if (person.getCurrentPos().distance(start) < closestPoint.distance(start))
                closestPoint = person.getCurrentPos();
        }
        return closestPoint;
    }

    public double getClosestEntityDistance(Vector2d start, Vector2d ignoreSelf) {
        return getClosestEntity(start, ignoreSelf).distance(start);
    }

    public INavigator getNavigator() {
        if (attractionEnabled) return attractionNavigator;
        return this.navigator;
    }

    public IPathFinder getPathFinder() {
        return pathfinder;
    }

    private IPathFinder loadPathFinder() {
        switch (props.getProperty("pathFinder")) {
            case "PreBuiltPathFinder":
                return new PreBuiltPathFinder(this);
            case "CrowdAwareNavigator":
                return new CrowdAwareNavigator(this);
            default:
                throw new Error("Invalid pathFinder set in properties!");
        }
    }
    private INavigator loadNavigator() {
        switch (props.getProperty("navigator")) {
            case "PreBuiltPathFinder":
                return new PreBuiltPathFinder(this);
            case "CrowdAwareNavigator":
                return new CrowdAwareNavigator(this);
            default:
                throw new Error("Invalid navigator set in properties!");
        }
    }

    private void loadScene() {
        SceneLoader sl = SceneLoader.getInstance();
        obstacles.addAll(sl.getObstacles());
        exit = sl.getExit();
    }

    private static Properties props;
    public static Properties getProps() {
        // read properties
        if (props != null) return props;

        props = new Properties();
        try {
            InputStream in = GamePanel.class.getResourceAsStream("exitsim.properties");
            props.load(in);
        } catch (IOException e) { throw new Error("exitsim.properties not valid!"); }
        return props;
    }
    ZoomController zoomController = ZoomController.getInstance();

    boolean attractionEnabled = false;
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            attractionNavigator.setAttract();
        else if (e.getButton() == MouseEvent.BUTTON3)
            attractionNavigator.setScatter();
        else if (e.getButton() == MouseEvent.BUTTON2)
            zoomController.setScale(1);
            zoomController.setTranslateX(0);
            zoomController.setTranslateY(0);

        attractionEnabled = true;
    }
    public void mouseReleased(MouseEvent e) {
        attractionEnabled = false;
    }
    public void mouseDragged(MouseEvent e) {
        attractionNavigator.setAttractionPoint(new Vector2d(e.getX(), e.getY()));
    }

    // Unused events
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        ZoomController zoomController = ZoomController.getInstance();
        int W = SimulationCanvas.W;
        int H = SimulationCanvas.H;
        double transX = zoomController.getTranslateX();
        double transY = zoomController.getTranslateY();
        double zoomFactor = 1.1;

        if (e.getWheelRotation()<0) {
            //Zoom in
            zoomController.setScale(zoomController.getScale() *zoomFactor);
            double scale = zoomController.getScale();

            zoomController.setTranslateX(transX + e.getX()/scale - e.getX()/scale*zoomFactor);
            zoomController.setTranslateY(transY + e.getY()/scale - e.getY()/scale*zoomFactor);

            //Nur innerhalb des Levels verschieben
            if(zoomController.getTranslateX() < - W + W/scale) zoomController.setTranslateX(- W + W/scale);
            if(zoomController.getTranslateY() < - H + H/scale) zoomController.setTranslateY(- H + H/scale);
            if(zoomController.getTranslateX() > 0) zoomController.setTranslateX(0);
            if(zoomController.getTranslateY() > 0) zoomController.setTranslateY(0);

        }else{
            //Zoom out
            zoomController.setScale(zoomController.getScale() / zoomFactor);

            //Nicht weiter herauszoomen als Originalgroesse
            if (zoomController.getScale() < 1)
            {
                zoomController.setScale(1);
                zoomController.setTranslateX(0);
                zoomController.setTranslateY(0);
            } else {
                double scale = zoomController.getScale();

                zoomController.setTranslateX(transX + e.getX()/scale - e.getX()/scale/zoomFactor);
                zoomController.setTranslateY(transY + e.getY()/scale - e.getY()/scale/zoomFactor);

                //Nur innerhalb des Levels verschieben
                if(zoomController.getTranslateX() < - W + W/scale) zoomController.setTranslateX(- W + W/scale);
                if(zoomController.getTranslateY() < - H + H/scale) zoomController.setTranslateY(- H + H/scale);
                if(zoomController.getTranslateX() > 0) zoomController.setTranslateX(0);
                if(zoomController.getTranslateY() > 0) zoomController.setTranslateY(0);
            }

        }
    }
}
