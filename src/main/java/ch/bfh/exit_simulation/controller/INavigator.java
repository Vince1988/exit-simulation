package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.util.Vector2d;

/**
 * Created by Shylux on 30.10.2015.
 */
public interface INavigator {
    /**
     * Gives directions for an entity through the level. Usually to the exit.
     * @param startNode The current position of the entity.
     * @return Direction in which the entity should go. Represented as Vector2d with length 1 (unit).
     */
    public Vector2d getDirection(Vector2d startNode);
}
