package com.nicktoony.cstopdown.networking.server;

/**
 * Created by nick on 13/07/15.
 */

import com.nicktoony.cstopdown.Strings;
import com.nicktoony.cstopdown.mods.CSServerPlayerWrapper;
import com.nicktoony.cstopdown.mods.gamemode.GameModeMod;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.mods.gamemode.implementations.LastTeamStanding;
import com.nicktoony.cstopdown.mods.gamemode.implementations.Left4Dead;
import com.nicktoony.cstopdown.mods.gamemode.implementations.TeamDeathMatch;
import com.nicktoony.cstopdown.mods.gamemode.implementations.Zombies;
import com.nicktoony.cstopdown.networking.packets.game.ChatPacket;
import com.nicktoony.cstopdown.networking.packets.game.PlayerDetailsPacket;
import com.nicktoony.cstopdown.networking.packets.helpers.PlayerDetailsWrapper;
import com.nicktoony.cstopdown.rooms.game.CSRoomGame;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.MyGame;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.networking.client.FakeClientSocket;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.networking.server.ServerClientHandler;
import com.nicktoony.engine.packets.connection.LoadedPacket;
import com.nicktoony.engine.rooms.RoomGame;
import com.nicktoony.engine.services.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class CSServer extends Server<CSServerClientHandler> {

    private RoomGame roomGame;
    private long roundTimer = 0;
    private long lastTime;
    private float delta;
    private List<PlayerDetailsWrapper> changed = new ArrayList<PlayerDetailsWrapper>();
    private Random random = new Random();

    public enum STATE {
        ROUND_START,
        ROUND_FREEZETIME,
        ROUND,
        ROUND_END
    }
    private STATE roundState;

    private List<GameModeMod> mods = new ArrayList<GameModeMod>();

    /**
     * Create a new CSTDServer with the given logger and config
     *
     * This is useful for defining the server setup ingame
     * @param logger
     * @param config
     */
    public CSServer(Logger logger, ServerConfig config, Server.LoopManager loopManager, MyGame.PlatformProvider platformProvider) {
        super(logger, config, loopManager);

        this.platformProvider = platformProvider;

        logger.log("Server started up");
//        // Game room that loads the map, validates collisions/movement
        roomGame = new CSRoomGame(new FakeClientSocket(config));
        roomGame.create(false);

        // begin server
        startServerSocket(config.sv_port);

        // A Java timer currently manages the game loop.. not ideal.
        loopManager.startServerLoop(this);

        this.lastTime = System.currentTimeMillis();

        // Decide which game mode (temporary implementation)
        GameModeMod gameModeMod = new LastTeamStanding();
        if (config.sv_mode.contentEquals("TeamDeathmatch")) {
            gameModeMod = new TeamDeathMatch();
        } else if (config.sv_mode.contentEquals("LastTeamStanding")) {
            gameModeMod = new LastTeamStanding();
        } else if (config.sv_mode.contentEquals("Zombies")) {
            gameModeMod = new Zombies();
        } else if (config.sv_mode.contentEquals("Left4Dead")) {
            gameModeMod = new Left4Dead();
        }
        gameModeMod.setup(this);
        mods.add(gameModeMod);

        notifyModInit();

        // Begin the game
        startRound();

        for (int i = 0; i < config.sv_bots; i ++) {
            CSServerClientHandler client = new CSServerClientHandlerBot(this);
            handleClientConnected(client);
            client.setState(ServerClientHandler.STATE.LOADING);
            notifyClientMessage(client, new LoadedPacket());

            String name;
            while (true) {
                name = config.sv_bot_prefix + Strings.BOT_NAMES[random.nextInt(Strings.BOT_NAMES.length)];
                boolean okay = true;
                for (CSServerClientHandler otherPlayer : getClients()) {
                    if (otherPlayer.getPlayerWrapper().getPlayerDetails().name.contentEquals(name)) {
                        okay = false;
                    }
                }
                if (okay) {
                    break;
                }
            }
            client.getPlayerWrapper().getPlayerDetails().name = name;
        }
    }

    public void step() {
        super.step();

        long now = System.currentTimeMillis();
        delta = (float) ((now - lastTime) / MyGame.MS_PER_TICK);
        lastTime = now;

//        System.out.println(delta + " :: " + fps);

        // Update world
        roomGame.step(delta);

        // Manages round time
        roundStep();

        changed.clear();
        for (CSServerClientHandler client : getClients()) {
            PlayerDetailsWrapper wrapper = client.getPlayerWrapper().getPlayerDetails();
            if (wrapper.changed) {
                changed.add(wrapper);
                wrapper.changed = false;
            }
        }
        if (changed.size() != 0) {
            PlayerDetailsPacket packet = new PlayerDetailsPacket();
            packet.playerDetails = changed.toArray(new PlayerDetailsWrapper[changed.size()]);
            packet.left = new int[0];
            sendToAll(packet);
        }

//        fps ++;
//        if ((now - fpsLast) >= 1000) {
//            logger.log("Server FPS:" + fps);
//            logger.log("Server delta: " + delta);
//            logger.log("Server ms-per-tick:" + MS_PER_TICK);
//            fps = 0;
//            fpsLast = now;
//        }
    }

    private void roundStep() {
        switch (roundState) {
            case ROUND_START:
//                notifyModRoundStart();
                roundState = STATE.ROUND_FREEZETIME;
                break;

            case ROUND_FREEZETIME:
                if ((config.mp_freeze_time*1000) + roundTimer < System.currentTimeMillis()) {
                    roundState = STATE.ROUND;
                    roundTimer = System.currentTimeMillis();
                    notifyModFreezeTime();
                }
                break;

            case ROUND:
                if ((config.mp_round_time*1000) + roundTimer < System.currentTimeMillis()) {
                    endRound();
                }
                break;

            case ROUND_END:
                if ((config.mp_victory_time*1000) + roundTimer < System.currentTimeMillis()) {
                    startRound();
                }
                break;
        }

        notifyModStep();
    }



    public RoomGame getGame() {
        return roomGame;
    }

    public void startRound() {
        roundState = STATE.ROUND_START;
        roundTimer = System.currentTimeMillis();

        notifyModRoundStart();
    }

    public void endRound() {
        roundState = STATE.ROUND_END;
        roundTimer = System.currentTimeMillis();

        notifyModRoundEnd();
    }

    public STATE getRoundState() {
        return roundState;
    }

    public float getDelta() {
        return delta;
    }

    public RoomGame getRoom() {
        return roomGame;
    }

    public void notifyModInit() {
        for (GameModeMod mod : mods) {
            mod.evInit();
        }
    }

    public void notifyModRoundStart() {
        sendToAll(new ChatPacket("[YELLOW]Round Started. Press B to buy weapons."));

        for (GameModeMod mod : mods) {
            mod.evRoundStart();
        }
    }

    public void notifyModFreezeTime() {
        sendToAll(new ChatPacket("[YELLOW]Fight!"));

        for (GameModeMod mod : mods) {
            mod.evFreezeTimeEnd();
        }
    }

    public void notifyModRoundEnd() {
        sendToAll(new ChatPacket("[YELLOW]Round Ended. Press M to join/change teams."));

        for (GameModeMod mod : mods) {
            mod.evRoundEnd();
        }
    }

    public void notifyModPlayerConnected(PlayerModInterface player) {
        sendToAll(new ChatPacket("[YELLOW]" + player.getName() + " connected."));

        for (GameModeMod mod : mods) {
            mod.evPlayerConnected(player);
        }
    }

    public void notifyModPlayerSpawned(CSServerPlayerWrapper player) {
        for (GameModeMod mod : mods) {
            mod.evPlayerSpawned(player);
        }
    }

    public void notifyModPlayerJoinedTeam(PlayerModInterface player, boolean forced) {
        sendToAll(new ChatPacket("[YELLOW]" + player.getName() + " joined " + EngineConfig.getTeamName(player.getTeam()) + "."));

        for (GameModeMod mod : mods) {
            mod.evPlayerJoinedTeam(player, forced);
        }
    }

    public void notifyModPlayerDestroyed(PlayerModInterface player) {
        for (GameModeMod mod : mods) {
            mod.evPlayerDestroyed(player);
        }
    }

    public void notifyModStep() {
        for (GameModeMod mod : mods) {
            mod.evStep();
        }
    }

    public void notifyModPlayerKilled(PlayerModInterface playerKilled, PlayerModInterface playerKiller) {
//        sendToAll(new ChatPacket("[YELLOW]" + playerKilled.getName() + " killed by " + playerKiller.getName() + "."));

        for (GameModeMod mod : mods) {
            mod.evPlayerKilled(playerKilled, playerKiller);
        }
    }

    public CSServerClientHandler findClientForPlayer(Player entityHit) {
        for (CSServerClientHandler client : getClients()) {
            if (client.getPlayer() == entityHit) {
                return client;
            }
        }

        return null;
    }

    public void notifyModPlayerShot(PlayerModInterface shooter, PlayerModInterface shot, int damage, boolean valid) {
        if (valid && shooter.getTeam() == shot.getTeam()) {
            sendToAll(new ChatPacket("[YELLOW]" + shooter.getName() + " shot a team mate."));
        }

        for (GameModeMod mod : mods) {
            mod.evPlayerShot(shooter, shot, damage, valid);
        }
    }

    public long getRoundSeconds() {
        return (System.currentTimeMillis() - roundTimer)/1000;
    }
}

