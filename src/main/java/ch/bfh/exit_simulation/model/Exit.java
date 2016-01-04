package ch.bfh.exit_simulation.model;

import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.*;

/**
 * Created by Shylux on 19.10.2015.
 */
public class Exit extends ObstaclePoly {
    public Vector2d getNavigationPoint() {
        return new Vector2d(centerPoint());
    }
}
