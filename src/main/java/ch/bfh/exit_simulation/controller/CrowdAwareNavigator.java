package ch.bfh.exit_simulation.controller;

import ch.bfh.exit_simulation.GamePanel;
import ch.bfh.exit_simulation.util.Vector2d;

/**
 * This Navigator takes other Persons into consideration when giving directions.
 * It will look forward, forward-right and forward-left and pick the direction with the most
 * amount of empty space to allow faster walking speed.
 *
 * Created by Shylux on 01.12.2015.
 */
public class CrowdAwareNavigator extends PreBuiltPathFinder {
    public CrowdAwareNavigator(GamePanel _panel) {
        super(_panel);
    }

    public Vector2d getDirection(Vector2d startNode) {
        Vector2d pathDirection = super.getDirection(startNode);

        double personRadius = Double.parseDouble(panel.getProps().getProperty("personRadius"));
        double lookAheadDistance = personRadius * 5;
        Vector2d lookAhead = startNode.add(pathDirection.normalize().scale(lookAheadDistance));


        Vector2d closestPointToLookAhead = panel.getClosestEntity(lookAhead, startNode);
        Vector2d correctionVector = lookAhead.sub(closestPointToLookAhead);
        correctionVector = correctionVector.setMagnitude(1/(correctionVector.magnitude()/3) * personRadius * 10).setMaxMagnitude(personRadius*4);
        lookAhead = lookAhead.add(correctionVector);
        Vector2d lookAheadRel = lookAhead.sub(startNode);

        return lookAheadRel.normalize();
    }
}
