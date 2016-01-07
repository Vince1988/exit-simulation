package ch.bfh.exit_simulation;

import ch.bfh.exit_simulation.controller.*;
import ch.bfh.exit_simulation.model.Ball;
import ch.bfh.exit_simulation.model.Exit;
import ch.bfh.exit_simulation.model.IObstacle;
import ch.bfh.exit_simulation.model.ObstaclePoly;
import ch.bfh.exit_simulation.util.Vector2d;
import ch.bfh.exit_simulation.view.BallRenderer;
import ch.bfh.exit_simulation.view.ObstaclePolyRenderer;
import javafx.scene.transform.Scale;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vincent Genecand on 21.09.2015.
 */
public class GamePanel implements MouseListener, MouseMotionListener, MouseWheelListener {

    private Set<Ball> balls;
    public Set<ObstaclePoly> obstacles;
    public Exit exit;
    private List<Line2D> obstacleNavLineCache;

    private PreBuiltPathFinder pathfinder;
    private AttractionNavigator attractionNavigator;

    private static GamePanel _instance;
    public static GamePanel getInstance() {
        if (_instance == null)
            _instance = new GamePanel();
        return _instance;
    }
    private GamePanel() {
        this.balls = new HashSet<>();
        this.obstacles = new HashSet<>();
        this.exit = new Exit(0, 0, 10, 50);

//        IntStream.range(0,10).forEach(x -> this.balls.add(Ball.createGenericBall(this.balls.size())));
//        IntStream.range(0,50).forEach(x -> this.balls.add(Ball.createRandomBall()));
        this.balls.addAll(Ball.createCardinalBalls());

        this.obstacles.addAll(ObstaclePoly.createDemoObstacles());

        this.pathfinder = new PreBuiltPathFinder(this);
        this.attractionNavigator = new AttractionNavigator();
    }

    public void update() {
        this.balls.forEach(ball -> new BallController(ball, this).update());
        List<Ball> ballsToCheck = new ArrayList<>(this.balls);
        for (Ball b : this.balls) {
            ballsToCheck.remove(b);
            ballsToCheck.forEach(ball -> new BallController(b, this).elasticCollision(ball));
        }
    }

    public void render(Graphics2D g, float interpolation) {
        this.balls.forEach(ball -> new BallRenderer(ball).render(g, interpolation));
        this.obstacles.forEach(obstacle -> new ObstaclePolyRenderer(obstacle).render(g, interpolation));
        g.setColor(Color.green);
        getObstacleNavigationLines().forEach(line -> g.draw(line));
        g.setColor(Color.BLUE);
        g.fill(this.exit);

        for (Ball b: this.balls) {
            List<Vector2d> path = pathfinder.getPathToExit(b.getCurrentPos());
            if (path == null) continue;
            for (int i = 0; i < path.size()-1; i++) {
                g.draw(new Line2D.Double(path.get(i).getPoint(), path.get(i+1).getPoint()));
            }
        }
//        for (Vector2d key: this.pathfinder.navTree.keySet()) {
//            Vector2d val = this.pathfinder.navTree.get(key);
//            if (val != null)
//                g.draw(new Line2D.Double(key.getPoint(), val.getPoint()));
//        }
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

    public INavigator getNavigator() {
        if (attractionEnabled) return attractionNavigator;
        return this.pathfinder;
    }

    ZoomController zoomController = ZoomController.getInstance();

    boolean attractionEnabled = false;
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            attractionNavigator.setAttract();
        else if (e.getButton() == MouseEvent.BUTTON3)
            attractionNavigator.setScatter();
        else if (e.getButton() == MouseEvent.BUTTON2)
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

            zoomController.setTranslateX(transX + ((W/scale/2) - (e.getX()/scale*zoomFactor)));
            zoomController.setTranslateY(transY + ((H/scale/2) - (e.getY()/scale*zoomFactor)));

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

                zoomController.setTranslateX(transX + ((W/scale/2) - (e.getX()/scale/zoomFactor)));
                zoomController.setTranslateY(transY + ((H/scale/2) - (e.getY()/scale/zoomFactor)));

                //Nur innerhalb des Levels verschieben
                if(zoomController.getTranslateX() < - W + W/scale) zoomController.setTranslateX(- W + W/scale);
                if(zoomController.getTranslateY() < - H + H/scale) zoomController.setTranslateY(- H + H/scale);
                if(zoomController.getTranslateX() > 0) zoomController.setTranslateX(0);
                if(zoomController.getTranslateY() > 0) zoomController.setTranslateY(0);
            }

        }
    }
}
