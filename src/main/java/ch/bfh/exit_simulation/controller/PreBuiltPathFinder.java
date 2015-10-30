package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.GamePanel;
import ch.bfh.exit_simulation.model.IObstacle;
import ch.bfh.exit_simulation.util.Vector2d;

import java.awt.geom.Line2D;
import java.util.*;

/**
 * Created by Shylux on 19.10.2015.
 */
public class PreBuiltPathFinder implements IPathFinder, INavigator {
    GamePanel panel;

    // for each node the next node to reach the exit.
    public static Map<Vector2d, Vector2d> navTree;
    // info on how far it is to the exit
    private static Map<Vector2d, Double> pathLength;

    public PreBuiltPathFinder(GamePanel _panel) {
        this.panel = _panel;

        if (navTree == null) {
            // Build navigation tree
            navTree = new HashMap<>();
            pathLength = new HashMap<>();
            List<Vector2d> allNodes = new ArrayList<>();
            for (IObstacle obstacle: panel.obstacles)
                allNodes.addAll(obstacle.getNavigationPoints());
            Vector2d exit = panel.exit.getNavigationPoint();
            navTree.put(exit, null);
            pathLength.put(exit, 0d);
            // working list contains nodes which need to be connected to its neighbours
            LinkedList<Vector2d> workingList = new LinkedList<>();
            workingList.add(exit);

            while (workingList.size() > 0) {
                Vector2d currentNode = workingList.removeFirst();
                for (Vector2d nextNode: allNodes) {
                    if (currentNode.equals(nextNode)) continue; // we don't want to travel to itself

                    // Check if connection between nodes is possible
                    if (collides(currentNode, nextNode)) continue;

                    // check if connection is better than a potential existing one
                    double distance = currentNode.sub(nextNode).magnitude();
                    if (navTree.containsKey(nextNode)) {
                        if (pathLength.get(nextNode) < pathLength.get(currentNode)+distance) continue;
                    }

                    // update neighbour node and mark it to be checked again
                    navTree.put(nextNode, currentNode);
                    pathLength.put(nextNode, pathLength.get(currentNode)+distance);
                    workingList.add(nextNode);
                }
            }
        }
    }

    private boolean collides(Vector2d from, Vector2d to) {
        Line2D line = new Line2D.Double(from.getPoint(), to.getPoint());
        for (IObstacle obstacle: panel.obstacles) {
            if (obstacle.collides(line)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Vector2d> getPathToExit(Vector2d startNode) {
        Vector2d bestEntryNode = getBestEntryNode(startNode);

        if (bestEntryNode == null)
            // uh oh.. are you inside an obstacle?
            return null;

        // build path
        LinkedList<Vector2d> path = new LinkedList<>();
        path.add(startNode);
        path.add(bestEntryNode);
        while (true) {
            Vector2d nextNode = navTree.get(path.getLast());
            if (nextNode == null) break;
            path.add(nextNode);
        }
        return path;
    }

    /**
     * If the entity is inside a structure there will be no entry point since its walled off on all sides.
     * @param startNode
     * @return
     */
    private Vector2d getBestEntryNode(Vector2d startNode) {
        Vector2d bestEntryNode = null;

        // find best entry node
        double bestPathLength = Double.MAX_VALUE;
        for (Vector2d entryNode: navTree.keySet()) {
            if (collides(startNode, entryNode)) continue;
            double pathDistance = pathLength.get(entryNode) + startNode.distance(entryNode);
            if (pathDistance < bestPathLength) {
                bestEntryNode = entryNode;
                bestPathLength = pathDistance;
            }
        }
        return bestEntryNode;
    }

    @Override
    public Vector2d getDirection(Vector2d startNode) {
        Vector2d entryNode = getBestEntryNode(startNode);
        if (entryNode == null) return Vector2d.ZERO;
        return getPathToExit(startNode).get(1).sub(startNode).normalize();
    }
}
