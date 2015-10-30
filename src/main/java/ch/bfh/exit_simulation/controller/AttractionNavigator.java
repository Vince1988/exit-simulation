package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.GamePanel;
import ch.bfh.exit_simulation.util.Vector2d;

/**
 * Created by Shylux on 30.10.2015.
 */
public class AttractionNavigator implements INavigator {

    private Vector2d attractionPoint = Vector2d.ZERO;
    private boolean scatter = false;

    public void setAttractionPoint(Vector2d newPoint) {
        attractionPoint = newPoint;
    }

    public void setScatter() {
        this.scatter = true;
    }
    public void setAttract() {
        this.scatter = false;
    }

    @Override
    public Vector2d getDirection(Vector2d startNode) {
        Vector2d direc = attractionPoint.sub(startNode).normalize();
        return (scatter) ? direc.negate() : direc;
    }
}
