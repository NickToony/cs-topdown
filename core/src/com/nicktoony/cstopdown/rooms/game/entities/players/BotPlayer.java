package com.nicktoony.cstopdown.rooms.game.entities.players;

import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.nicktoony.cstopdown.mods.CSServerPlayerWrapper;
import com.nicktoony.cstopdown.networking.packets.player.PlayerSwitchWeapon;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.networking.server.CSServerClientHandler;
import com.nicktoony.engine.entities.world.PathfindingHeuristic;
import com.nicktoony.engine.entities.world.PathfindingNode;
import com.nicktoony.engine.entities.world.PathfindingPath;
import com.nicktoony.engine.entities.world.PathfindingRaycastCollisionDetector;

import java.util.ArrayList;
import java.util.List;
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

    private static final int BOT_REACTION_MIN = 30;
    private static final int BOT_REACTION_MAX = 60;

    private Random random = new Random();
    private PathfindingPath path;
    private int pathIndex = 0;
    private PathFinder<PathfindingNode> pathFinder;
    private PathfindingHeuristic heuristic;
    private AIState aiState = AIState.idle;
    private PathSmoother<PathfindingNode, Vector2> pathSmoother;
    private CSServer server;
    private CSServerClientHandler player;
    private List<CSServerPlayerWrapper> targets = new ArrayList<CSServerPlayerWrapper>();
    private CSServerPlayerWrapper currentTarget = null;
    private int lastScan = 0;

    public void setupBot(CSServer server, CSServerClientHandler player) {
        this.server = server;
        this.player = player;
    }

    @Override
    public void create(boolean render) {
        super.create(render);

        pathFinder = new IndexedAStarPathFinder<PathfindingNode>(getRoom().getMap().getPathfindingGraph(), true);
        path = new PathfindingPath();
        heuristic = new PathfindingHeuristic();
        pathSmoother = new PathSmoother<PathfindingNode, Vector2>(
                new PathfindingRaycastCollisionDetector(getRoom().getWorld()));
    }

    @Override
    public void step(float delta) {
        super.step(delta);

        lastScan --;
        if (lastScan < 0) {
            scanForEnemies();

            lastScan = BOT_REACTION_MIN + random.nextInt(BOT_REACTION_MAX - BOT_REACTION_MIN);
        }

        if (!targets.isEmpty()) {
            aiState = AIState.combat;
        }

        switch (aiState) {
            case idle:
                moveRight = false;
                moveLeft = false;
                moveUp = false;
                moveDown = false;
                shootKey = false;
                reloadKey = false;

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

                    // Tell client
                    PlayerSwitchWeapon playerSwitchWeapon = new PlayerSwitchWeapon();
                    playerSwitchWeapon.timestamp = player.getTimestamp();
                    playerSwitchWeapon.slot = random.nextInt(getWeapons().length);
                    player.handleReceivedMessage(playerSwitchWeapon);
                }
                break;

            case moving:
                PathfindingNode targetNode = path.get(pathIndex);
                if (new Vector2(x, y).dst(targetNode.getWorldPos()) <= 20) {
                    pathIndex += 1;
                    if (pathIndex >= path.getCount()) {
                        aiState = AIState.idle;
                    }
                } else {
                    moveRight = x + 8 < targetNode.getWorldX();
                    moveLeft = x - 8 > targetNode.getWorldX();
                    moveUp = y + 8 < targetNode.getWorldY();
                    moveDown = y - 8 > targetNode.getWorldY();
                    directionTo = (float) Math.toDegrees(Math.atan2(targetNode.getWorldY() - y,
                            targetNode.getWorldX() - x)) - 90;
                }
                break;

            case combat:
                if (targets.isEmpty()) {
                    aiState = AIState.idle;
                    break;
                }

                if (currentTarget != null && currentTarget.isAlive()) {
                    moveRight = false;
                    moveLeft = false;
                    moveUp = false;
                    moveDown = false;
                    shootKey = true;
                    reloadKey = (getCurrentWeaponObject().bulletsIn <= 0);

                    directionTo = (float) Math.toDegrees(Math.atan2(currentTarget.getY() - y,
                            currentTarget.getX() - x)) - 90;
                } else {
                    currentTarget = targets.get(0);
                }


                break;
        }
    }

    private void scanForEnemies() {
        targets.clear();
        for (CSServerClientHandler otherPlayer : server.getClients()) {
            if (otherPlayer.getPlayerWrapper().isAlive()
                    && otherPlayer.getPlayerWrapper().getTeam() != player.getPlayerWrapper().getTeam()) {
                if (canSeePlayer(otherPlayer.getPlayer())) {
                    targets.add(otherPlayer.getPlayerWrapper());
                }
            }
        }
    }
}
