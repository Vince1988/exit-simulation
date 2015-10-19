package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.util.Vector2d;

import java.util.List;

/**
 * Created by Shylux on 19.10.2015.
 */
public interface IPathFinder {
    /**
     * Searches full path around obstacles to exit.
     * @param startNode The position where the algorithm should start.
     * @return full path around obstacles to exit
     */
    public List<Vector2d> getPathToExit(Vector2d startNode);
}
