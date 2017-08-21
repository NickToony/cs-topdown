package com.nicktoony.cstopdown.rooms.game.entities.players;

import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.nicktoony.cstopdown.mods.CSServerPlayerWrapper;
import com.nicktoony.cstopdown.networking.packets.player.PlayerSwitchWeapon;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.networking.server.CSServerClientHandler;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.MyGame;
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
        moving,
        exploring
    }

    private static final int BOT_REACTION_MIN = 30;
    private static final int BOT_REACTION_MAX = 60;

    private Random random = new Random();
    private PathfindingPath path;
    private int pathIndex = 0;
    private PathFinder<PathfindingNode> pathFinder;
    private PathfindingHeuristic heuristic;
    private Vector2 pathGoal;
    private AIState aiState = AIState.idle;
    private PathSmoother<PathfindingNode, Vector2> pathSmoother;
    private CSServer server;
    private CSServerClientHandler player;
    private List<CSServerPlayerWrapper> targets = new ArrayList<CSServerPlayerWrapper>();
    private CSServerPlayerWrapper currentTarget = null;
    private int lastScan = 0;
    private int pause = 0;
    private Vector2 explorePosition = null;
    private Vector2 positionSinceLastScan;
    private int nearbyTargets = 0;

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

        positionSinceLastScan = getPosition();

        mouseDistance = 0;
    }

    @Override
    public void step(float delta) {
        super.step(delta);

        if (!server.getConfig().ai_enabled) {
            return;
        }

        lastScan --;
        if (lastScan < 0) {
            lastScan = nearbyTargets > 0 ? 10 : 30;
            if (pause <= 0 || aiState != AIState.combat) {
                pause = BOT_REACTION_MIN + random.nextInt(BOT_REACTION_MAX - BOT_REACTION_MIN);
                if (aiState == AIState.combat) {
                    pause /= 2;
                }
            }

            scanForEnemies();

//            if (aiState == AIState.combat) { pause /= 2; }

            // If the bot appears to be stuck
            if (aiState == AIState.moving || aiState == AIState.exploring) {
                if (EngineConfig.toPixels(positionSinceLastScan.dst(getPosition())) < 1) {
                    // Try doing something else
                    aiState = AIState.idle;
                }
            }
            positionSinceLastScan = getPosition();
        }

        if (!targets.isEmpty()) {
            aiState = AIState.combat;
        } else if (explorePosition != null && aiState != AIState.exploring) {
            if (player.getPlayer().getCurrentWeaponObject().bulletsIn > player.getPlayer().getCurrentWeaponObject().getWeapon(getRoom().getWeaponManager()).getClipSize() * .2) {
                PathfindingNode node = getRoom().getMap().getPathfindingGraph()
                        .getNodeByWorld(explorePosition.x, explorePosition.y);
                if (startPath(node)) {
                    aiState = AIState.exploring;
                }
            }
        }

        switch (aiState) {
            case idle:
                moveRight = false;
                moveLeft = false;
                moveUp = false;
                moveDown = false;
                shootKey = false;
                reloadKey = (player.getPlayer().getCurrentWeaponObject().bulletsIn < player.getPlayer().getCurrentWeaponObject().getWeapon(getRoom().getWeaponManager()).getClipSize() * .8);

                PathfindingNode node = null;
                while (node == null || node.isSolid()) {
                    node = getRoom().getMap().getPathfindingGraph()
                            .getNode(random.nextInt(getRoom().getMap().getPathfindingGraph().getNodeCount()));
                }

                if (startPath(node)) {
                    aiState = AIState.moving;

                    // Tell client
                    int nextWeapon = random.nextInt(getWeapons().length);
                    if (nextWeapon != getCurrentWeapon()) {
                        PlayerSwitchWeapon playerSwitchWeapon = new PlayerSwitchWeapon();
                        playerSwitchWeapon.setTimestamp(player.getTimestamp());
                        playerSwitchWeapon.slot = nextWeapon;
                        player.handleReceivedMessage(playerSwitchWeapon);
                    }
                }

                break;

            case moving:
                movePath();
                break;

            case exploring:
                movePath();

                shootKey = false;

                if (pathGoal != null) {
                    directionTo = (float) Math.toDegrees(Math.atan2(pathGoal.y - y,
                            pathGoal.x - x)) - 90;
                }
                break;

            case combat:
                if (targets.isEmpty()) {
                    aiState = AIState.idle;
                    break;
                }

                if (currentTarget != null && currentTarget.isAlive()) {
                    if (pause > 0) pause --;

                    float distance = getPosition().dst(currentTarget.getPlayer().getPosition());
                    boolean inRange = (distance <= getCurrentWeaponObject().getWeapon(getRoom().getWeaponManager()).getRange() - 8
                            || getCurrentWeaponObject().getWeapon(getRoom().getWeaponManager()).getRange() == -1);

                    if (!inRange) {
                        moveRight = x + 8 < currentTarget.getX();
                        moveLeft = x - 8 > currentTarget.getX();
                        moveUp = y + 8 < currentTarget.getY();
                        moveDown = y - 8 > currentTarget.getY();
                    } else {
                        moveLeft = moveRight = moveUp = moveDown = false;
                    }
                    shootKey = (pause <= 0 && inRange);
                    reloadKey = (getCurrentWeaponObject().bulletsIn <= 0);

                    directionTo = (float) Math.toDegrees(Math.atan2(currentTarget.getY() - y,
                            currentTarget.getX() - x)) - 90;
                } else {
                    for (CSServerPlayerWrapper otherPlayer : targets) {
                        if (otherPlayer.isAlive()) {
                            currentTarget = otherPlayer;
                            break;
                        }
                    }
                }


                break;
        }
    }

    private void scanForEnemies() {
        targets.clear();
        currentTarget = null;
        explorePosition = null;
        nearbyTargets = 0;
        for (CSServerClientHandler otherPlayer : server.getClients()) {
            if (otherPlayer.getPlayerWrapper().isAlive()
                    && otherPlayer.getPlayerWrapper().getTeam() != player.getPlayerWrapper().getTeam()) {
                float distance = getPosition().dst(otherPlayer.getPlayer().getPosition());
                if (distance < getRoom().getSocket().getServerConfig().mp_bot_engage_range) {
                    if (canSeePlayer(otherPlayer.getPlayer())) {
                            targets.add(otherPlayer.getPlayerWrapper());
                    }
                    else if (otherPlayer.getPlayer().isMoving()) {
                        // we heard them...
                        explorePosition = otherPlayer.getPlayer().getPosition();
                        nearbyTargets ++;
//                        System.out.println("EXPLORE TIME¬!" + getPosition().dst(player.getPlayer().getPosition()));
                    }
                }
            }
        }
    }

    private boolean startPath(PathfindingNode node) {
        path.clear();
        boolean success = pathFinder.searchNodePath(
                getRoom().getMap().getPathfindingGraph().getNodeByWorld(x, y),
                node, heuristic, path);

        if (success) {
//            pathSmoother.smoothPath(path);
            pathIndex = 0;
            pathGoal = node.getWorldPos();
            return true;
        }

        return false;
    }

    private void movePath() {
        PathfindingNode targetNode = path.get(pathIndex);
        PathfindingNode currentNode = path.get(Math.max(0, pathIndex-2));
        if (new Vector2(x, y).dst(targetNode.getWorldPos()) <= 20) {
            pathIndex += 1;
            if (pathIndex >= path.getCount()) {
                aiState = AIState.idle;
                pathGoal = null;
            }
        } else {
            moveRight = x + 8 < targetNode.getWorldX();
            moveLeft = x - 8 > targetNode.getWorldX();
            moveUp = y + 8 < targetNode.getWorldY();
            moveDown = y - 8 > targetNode.getWorldY();
            directionTo = (float) Math.toDegrees(Math.atan2(targetNode.getWorldY() - currentNode.getWorldY(),
                    targetNode.getWorldX() - currentNode.getWorldX())) - 90;
        }
    }
}
