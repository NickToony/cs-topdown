package com.nicktoony.cstopdown.rooms.game.entities.world;

import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * Created by Nick on 24/03/2016.
 */
public class PathfindingHeuristic implements Heuristic<PathfindingNode> {
    @Override
    public float estimate(PathfindingNode node, PathfindingNode endNode) {
        return Math.abs(endNode.getX() - node.getX()) + Math.abs(endNode.getY() - node.getY());
    }
}
