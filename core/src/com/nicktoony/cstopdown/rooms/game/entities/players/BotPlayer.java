package com.nicktoony.cstopdown.rooms.game.entities.players;

import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.nicktoony.cstopdown.rooms.game.entities.world.PathfindingHeuristic;
import com.nicktoony.cstopdown.rooms.game.entities.world.PathfindingNode;
import com.nicktoony.cstopdown.rooms.game.entities.world.PathfindingPath;
import com.nicktoony.cstopdown.rooms.game.entities.world.PathfindingRaycastCollisionDetector;

import java.util.Random;

/**
 * Created by Nick on 24/03/2016.
 */
public class BotPlayer extends Player {

    enum AIState {
        idle,
        combat,
        moving
    }

    private Random random = new Random();
    private PathfindingPath path;
    private int pathIndex = 0;
    private PathFinder<PathfindingNode> pathFinder;
    private PathfindingHeuristic heuristic;
    private AIState aiState = AIState.idle;
    private PathSmoother<PathfindingNode, Vector2> pathSmoother;

    @Override
    public void create(boolean render) {
        super.create(render);

        pathFinder = new IndexedAStarPathFinder<>(getRoom().getMap().getPathfindingGraph(), true);
        path = new PathfindingPath();
        heuristic = new PathfindingHeuristic();
        pathSmoother = new PathSmoother<>(
                new PathfindingRaycastCollisionDetector(getRoom().getWorld()));
    }

    @Override
    public void step(float delta) {
        super.step(delta);

        switch (aiState) {
            case idle:
                PathfindingNode node = null;
                while (node == null || node.isSolid()) {
                    node = getRoom().getMap().getPathfindingGraph()
                            .getNode(random.nextInt(getRoom().getMap().getPathfindingGraph().getNodeCount()));
                }

                path.clear();
                boolean success = pathFinder.searchNodePath(
                        getRoom().getMap().getPathfindingGraph().getNodeByWorld(x, y),
                        node, heuristic, path);

                if (success) {
//                    pathSmoother.smoothPath(path);
                    aiState = AIState.moving;
                    pathIndex = 0;
                }
                break;

            case moving:
                PathfindingNode currentTarget = path.get(pathIndex);
                if (new Vector2(x, y).dst(currentTarget.getWorldPos()) < 8) {
                    pathIndex += 1;
                    if (pathIndex >= path.getCount()) {
                        aiState = AIState.idle;
                    }
                } else {
                    moveRight = x + 4 < currentTarget.getWorldX();
                    moveLeft = x - 4 > currentTarget.getWorldX();
                    moveUp = y + 4 < currentTarget.getWorldY();
                    moveDown = y - 4 > currentTarget.getWorldY();
                    directionTo = (float) Math.toDegrees(Math.atan2(currentTarget.getWorldY() - y, currentTarget.getWorldX() - x)) - 90;

//                    System.out.println(x + "->" + currentTarget.getWorldX() + ", " + y + "->" + currentTarget.getWorldY());
                }
                break;
        }
    }
}
