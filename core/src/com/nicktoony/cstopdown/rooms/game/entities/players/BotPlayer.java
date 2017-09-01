package com.nicktoony.cstopdown.rooms.game.entities.players;

import com.badlogic.gdx.ai.pfa.PathFinder;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;
import com.nicktoony.cstopdown.mods.CSServerPlayerWrapper;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.networking.packets.game.BuyWeaponPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerSwitchWeapon;
import com.nicktoony.cstopdown.networking.server.CSServer;
import com.nicktoony.cstopdown.networking.server.CSServerClientHandler;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.entities.world.PathfindingHeuristic;
import com.nicktoony.engine.entities.world.PathfindingNode;
import com.nicktoony.engine.entities.world.PathfindingPath;
import com.nicktoony.engine.entities.world.PathfindingRaycastCollisionDetector;
import com.nicktoony.engine.services.weapons.Weapon;
import com.nicktoony.engine.services.weapons.WeaponCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nick on 24/03/2016.
 */
public class BotPlayer extends Player {

    enum AIState {
        spawn,
        idle,
        combat,
        moving,
        hunting,
        camping,
        protecting
    }

    public static class BotTraits {
        public int protect = 10; // move to protect a friendly player
        public int assault = 20; // assault the enemy spawn
        public int camp = 10; // camp somewhere
        public int explore = 70; // explore randomly

        public BotTraits(int protect, int assault, int camp, int explore) {
            this.protect = protect;
            this.assault = assault;
            this.camp = camp;
            this.explore = explore;
        }

        public BotTraits() {
//            this(100, 0, 0, 0);
        }

        public int sum() {
            return protect + assault + camp + explore;
        }
    }

    private static final int BOT_REACTION_MIN = 30;
    private static final int BOT_REACTION_MAX = 60;
    private static final int BOT_PROTECT_RANGE = 5;
    private static final int BOT_PROTECT_RANGE_MIN = 3;
    private static final int BOT_CAMP_TIME_MIN = 200;
    private static final int BOT_CAMP_TIME_MAX = 400;
    private static final int BOT_PROTECT_TIME_MIN = 300;
    private static final int BOT_PROTECT_TIME_MAX = 700;

    private Random random = new Random();
    private PathfindingPath path;
    private int pathIndex = 0;
    private PathFinder<PathfindingNode> pathFinder;
    private PathfindingHeuristic heuristic;
    private Vector2 pathGoal;
    private AIState aiState = AIState.spawn;
    private PathSmoother<PathfindingNode, Vector2> pathSmoother;
    private CSServer server;
    private CSServerClientHandler player;
    private List<CSServerPlayerWrapper> targets = new ArrayList<CSServerPlayerWrapper>();
    private CSServerPlayerWrapper currentTarget = null;
    private int lastScan = 0;
    private int combatPause = 0;
    private int actionPause = 0;
    private Vector2 huntPosition = null;
    private Vector2 positionSinceLastScan;
    private int nearbyTargets = 0;
    private BotTraits botTraits = new BotTraits();
    private Player protectingPlayer = null;
    private boolean boughtWeapons = false;

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
        actionPause = random.nextInt(90) + 30;
        directionTo = random.nextInt(360);
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
            if (combatPause < 0
                    || aiState != AIState.combat) {
                combatPause = BOT_REACTION_MIN + random.nextInt(BOT_REACTION_MAX - BOT_REACTION_MIN);
                if (aiState == AIState.combat) {
                    combatPause /= 4;
                }
            }

            scanForEnemies();

            // If the bot appears to be stuck
            if (aiState == AIState.moving || aiState == AIState.hunting) {
                if (EngineConfig.toPixels(positionSinceLastScan.dst(getPosition())) < 1) {
                    // Try doing something else
                    aiState = AIState.idle;
                }
            }
            positionSinceLastScan = getPosition();
        }

        if (!targets.isEmpty()) {
            aiState = AIState.combat;
        } else if (huntPosition != null && aiState != AIState.hunting) {
            if (player.getPlayer().getCurrentWeaponObject().bulletsIn > player.getPlayer().getCurrentWeaponObject().getWeapon(getRoom().getWeaponManager()).getClipSize() * .2) {
                PathfindingNode node = getRoom().getMap().getPathfindingGraph()
                        .getNodeByWorld(huntPosition.x, huntPosition.y);
                if (startPath(node)) {
                    aiState = AIState.hunting;
                }
            }
        }

        switch (aiState) {
            // The bot needs weapons!
            case spawn:
                if (!boughtWeapons) {
                    // primary
                    buyRandomWeapon(PlayerModInterface.SECONDARY);
                    buyRandomWeapon(PlayerModInterface.PRIMARY);
                    boughtWeapons = true;
                }

                actionPause -= 1;
                if (actionPause <= 0) {
                    aiState = AIState.idle;
                }
                break;

            // The bot needs to decide what to do
            case idle:
                moveRight = false;
                moveLeft = false;
                moveUp = false;
                moveDown = false;
                shootKey = false;
                reloadKey = (player.getPlayer().getCurrentWeaponObject().bulletsIn < player.getPlayer().getCurrentWeaponObject().getWeapon(getRoom().getWeaponManager()).getClipSize() * .8);

                // Change weapon
                randomWeapon();

                // Now do something depending on traits
                int action = random.nextInt(botTraits.sum());
                if (action < botTraits.camp) {
                    actionCamp();
                } else if (action < botTraits.camp + botTraits.explore) {
                    actionExplore();
                } else if (action < botTraits.camp + botTraits.explore + botTraits.assault) {
                    actionAssault();
                } else if (action < botTraits.camp + botTraits.explore + botTraits.assault + botTraits.protect) {
                    List<Player> protectablePlayers = new ArrayList<Player>();
                    for (CSServerClientHandler client : server.getClients()) {
                        if (client.getPlayerWrapper().isAlive()
                                && client.getPlayerWrapper().getTeam() == getTeam()) {
                            // Potential protection target
//                            protectablePlayers.add(client.getPlayer());
                            // Is it a human?
                            if (!client.getPlayerWrapper().isBot()) {
                                // Double the chance!
                                protectablePlayers.add(client.getPlayer());
                            }
                        }
                    }

                    if (protectablePlayers.size() > 0) {
                        // Protect a random one
                        actionProtect(protectablePlayers.get(random.nextInt(protectablePlayers.size())));
                    } else {
                        // No humans?!?!
                        actionExplore(); // avenge!
                    }
                }

                break;

            // The bot is just moving somewhere
            case moving:
                shootKey = false;
                if (!movePath()) {
                    aiState = AIState.idle;
                }
                break;

            case camping:
                shootKey = false;
                if (!movePath()) {
                    stopMoving();
                    if (actionPause > 0) {
                        actionPause--;

                        if (actionPause % 60 == 0) {
                            directionTo = random.nextInt(360);
                        }
                    } else {
                        aiState = AIState.idle;
                    }
                }
                break;

            case protecting:
                shootKey = false;
                if (!movePath()) {
                    stopMoving();
                    if (actionPause > 0) {
                        actionPause--;

                        if (getPosition().dst(protectingPlayer.getPosition()) > (EngineConfig.CELL_SIZE * BOT_PROTECT_RANGE)) {
                            actionProtect(protectingPlayer);
                        } else {
                            if (actionPause % 60 == 0) {
                                directionTo = random.nextInt(360);
                            }
                        }
                    } else {
                        aiState = AIState.idle;
                    }
                }
                break;

            // The bot knows where an enemy is, and is now hunting them
            case hunting:
                if (!movePath()) {
                    aiState = AIState.idle;
                }

                shootKey = false;

                if (pathGoal != null) {
                    directionTo = (float) Math.toDegrees(Math.atan2(pathGoal.y - y,
                            pathGoal.x - x)) - 90;
                }
                break;

            // The bot is engaged in combat!
            case combat:
                if (targets.isEmpty()) {
                    aiState = AIState.idle;
                    break;
                }

                if (currentTarget != null && currentTarget.isAlive()) {
                    if (combatPause >= 0) combatPause--;

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
                    shootKey = (combatPause <= 0 && inRange);
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

    private void buyRandomWeapon(int slot) {
        List<String> keys = new ArrayList<String>();
        for (WeaponCategory category : getRoom().getWeaponManager().getWeaponCategories()) {
            for (Weapon weapon : category.getWeapons()) {
                if (weapon.getSlot() == slot) {
                    keys.add(weapon.getKey());
                }
            }
        }
        // Buy a gun
        player.handleReceivedMessage(new BuyWeaponPacket(keys.get(random.nextInt(keys.size()))));

    }

    private void actionExplore() {
        PathfindingNode node = null;
        while (node == null || node.isSolid()) {
            node = getRoom().getMap().getPathfindingGraph()
                    .getNode(random.nextInt(getRoom().getMap().getPathfindingGraph().getNodeCount()));
        }

        if (startPath(node)) {
            aiState = AIState.moving;
        }
    }

    private void actionAssault() {
        // Find players to hunt!
        List<Player> assaultablePlayer = new ArrayList<Player>();
        for (CSServerClientHandler client : server.getClients()) {
            if (client.getPlayerWrapper().isAlive()
                    && client.getPlayerWrapper().getTeam() != getTeam()) {
                // Potential protection target
                assaultablePlayer.add(client.getPlayer());
            }
        }
        if (!assaultablePlayer.isEmpty()) {
            huntPosition = assaultablePlayer.get(random.nextInt(assaultablePlayer.size())).getPosition();
        }
    }

    private void actionProtect(Player player) {
        PathfindingNode node = null;
        while (node == null || node.isSolid()) {
            int range = BOT_PROTECT_RANGE_MIN + random.nextInt(BOT_PROTECT_RANGE - BOT_PROTECT_RANGE_MIN);
            int xOffset = (random.nextInt(range * 2) - range) * EngineConfig.CELL_SIZE;
            int yOffset = (random.nextInt(range * 2) - range) * EngineConfig.CELL_SIZE;
            float xTarget = player.getX() + xOffset;
            float yTarget = player.getY() + yOffset;
            if (getRoom().getMap().isPointOnMap(xTarget, yTarget)) {
                node = getRoom().getMap().getPathfindingGraph()
                        .getNodeByWorld(xTarget, yTarget);
            }
        }

        if (startPath(node)) {
            actionPause = BOT_PROTECT_TIME_MIN + random.nextInt(BOT_PROTECT_TIME_MAX - BOT_PROTECT_TIME_MIN);
            protectingPlayer = player;
            aiState = AIState.protecting;
        }
    }

    private void actionCamp() {
        PathfindingNode node = null;
        while (node == null || node.isSolid()) {
            node = getRoom().getMap().getPathfindingGraph()
                    .getNode(random.nextInt(getRoom().getMap().getPathfindingGraph().getNodeCount()));
            // Check if it's a corner
            if (node.getConnections().size > 2) {
                // Too many connections! not a good camp spot!
                node = null;
            }
        }

        if (startPath(node)) {
            actionPause = BOT_CAMP_TIME_MIN + random.nextInt(BOT_CAMP_TIME_MAX - BOT_CAMP_TIME_MIN);
            aiState = AIState.camping;
        }
    }

    private void actionCampHere() {
//        PathfindingNode node = getRoom().getMap().getPathfindingGraph()
//                    .getNodeByWorld(getX(), getY());

        List<PathfindingNode> possibleNodes = new ArrayList<PathfindingNode>();
        for (int x = -4; x < 4; x ++) {
            for (int y = -4; y < 4; y ++) {
                int xOffset = x * EngineConfig.CELL_SIZE;
                int yOffset = y * EngineConfig.CELL_SIZE;
                float xTarget = getX() + xOffset;
                float yTarget = getY() + yOffset;
                if (getRoom().getMap().isPointOnMap(xTarget, yTarget)) {
                    PathfindingNode node = getRoom().getMap().getPathfindingGraph()
                            .getNodeByWorld(xTarget, yTarget);
                    // Check if it's a corner
                    if (node.getConnections().size <= 2) {
                        int pathLength = pathLength(node);
                        if (pathLength < 4 && pathLength != -1) {
                            possibleNodes.add(node);
                        }
                    }
                }
            }
        }

        if (!possibleNodes.isEmpty()) {
            if (startPath(possibleNodes.get(random.nextInt(possibleNodes.size())))) {
                actionPause = BOT_CAMP_TIME_MIN + random.nextInt(BOT_CAMP_TIME_MAX - BOT_CAMP_TIME_MIN);
                actionPause /= 3;
                aiState = AIState.camping;
            }
        }
    }

    private void randomWeapon() {
        // Tell client
//        int nextWeapon = random.nextInt(getWeapons().length);
//        if (nextWeapon != getCurrentWeapon()) {
//            PlayerSwitchWeapon playerSwitchWeapon = new PlayerSwitchWeapon();
//            playerSwitchWeapon.setTimestamp(player.getTimestamp());
//            playerSwitchWeapon.slot = nextWeapon;
//            player.handleReceivedMessage(playerSwitchWeapon);
//        }

        int nextWeapon;
        if (random.nextInt(10) <= 1) {
            nextWeapon = PlayerModInterface.SECONDARY;
        } else {
            nextWeapon = PlayerModInterface.PRIMARY;
        }

        if (nextWeapon != getCurrentWeapon()) {
            setNextWeapon(nextWeapon);
            PlayerSwitchWeapon playerSwitchWeapon = new PlayerSwitchWeapon();
            playerSwitchWeapon.setTimestamp(player.getTimestamp());
            playerSwitchWeapon.slot = nextWeapon;
            player.handleReceivedMessage(playerSwitchWeapon);
        }

    }

    private void scanForEnemies() {
        targets.clear();
        currentTarget = null;
        huntPosition = null;
        nearbyTargets = 0;
        for (CSServerClientHandler otherPlayer : server.getClients()) {
            if (otherPlayer.getPlayerWrapper().isAlive()
                    && otherPlayer.getPlayerWrapper().getTeam() != player.getPlayerWrapper().getTeam()) {
                float distance = getPosition().dst(otherPlayer.getPlayer().getPosition());
                if (distance < getRoom().getSocket().getServerConfig().mp_bot_engage_range) {
                    if (canSeePlayer(otherPlayer.getPlayer())) {
                            targets.add(otherPlayer.getPlayerWrapper());
                            nearbyTargets ++;
                    }
                    else if (otherPlayer.getPlayer().isMoving()
                            || otherPlayer.getPlayer().getShooting()) {
                        // we heard them... let's go hunt them (if not camping)
                        if (aiState != AIState.camping && aiState != AIState.protecting) {
                            actionRespond(otherPlayer.getPlayer().getPosition());
                            nearbyTargets++;
                        }
                    }
                }
            }
        }
    }

    private void actionRespond(Vector2 pos) {
        int action = random.nextInt(botTraits.sum());
        if (action < botTraits.camp + botTraits.protect) {
            actionCampHere();
        } else {
            huntPosition = pos;
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

    private int pathLength(PathfindingNode target) {
        PathfindingPath path = new PathfindingPath();
        boolean success = pathFinder.searchNodePath(
                getRoom().getMap().getPathfindingGraph().getNodeByWorld(x, y),
                target, heuristic, path);
        if (success) {
            return path.nodes.size;
        } else {
            return -1;
        }
    }

    /**
     *
     * @return boolean whether the bot is still moving along path
     */
    private boolean movePath() {
        if (pathGoal == null) {
            return false;
        }

        PathfindingNode targetNode = path.get(pathIndex);
        PathfindingNode currentNode = path.get(Math.max(0, pathIndex-2));
        if (new Vector2(x, y).dst(targetNode.getWorldPos()) <= 20) {
            pathIndex += 1;
            if (pathIndex >= path.getCount()) {
                pathGoal = null;
                return false;
            }
        } else {
            moveRight = x + 8 < targetNode.getWorldX();
            moveLeft = x - 8 > targetNode.getWorldX();
            moveUp = y + 8 < targetNode.getWorldY();
            moveDown = y - 8 > targetNode.getWorldY();
            directionTo = (float) Math.toDegrees(Math.atan2(targetNode.getWorldY() - currentNode.getWorldY(),
                    targetNode.getWorldX() - currentNode.getWorldX())) - 90;
        }

        return true;
    }

    private void stopMoving() {
        moveLeft = moveRight = moveUp = moveDown = false;
    }

    public void setTraits(BotTraits botTraits) {
        this.botTraits = botTraits;
    }
}
