package com.nicktoony.cstopdown.networking.server;

/**
 * Created by nick on 13/07/15.
 */

import com.nicktoony.cstopdown.mods.gamemode.implementations.LastTeamStanding;
import com.nicktoony.cstopdown.networking.packets.game.ChatPacket;
import com.nicktoony.cstopdown.rooms.game.CSRoomGame;
import com.nicktoony.engine.MyGame;
import com.nicktoony.cstopdown.mods.gamemode.GameModeMod;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.mods.gamemode.implementations.TeamDeathMatch;
import com.nicktoony.engine.rooms.RoomGame;
import com.nicktoony.engine.config.ServerConfig;
import com.nicktoony.engine.networking.client.FakeClientSocket;
import com.nicktoony.engine.networking.server.Server;
import com.nicktoony.engine.networking.server.ServerClientHandler;
import com.nicktoony.engine.packets.connection.LoadedPacket;
import com.nicktoony.engine.services.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class CSServer extends Server<CSServerClientHandler> {

    private RoomGame roomGame;
    private long roundTimer = 0;
    private long lastTime;
    private final double MS_PER_TICK = 1000 / MyGame.GAME_FPS;
    private float delta;
    private enum STATE {
        ROUND_START,
        ROUND,
        ROUND_END
    }
    private STATE roundState;

    private List<GameModeMod> mods = new ArrayList<GameModeMod>();

    private long fpsLast = 0;
    private int fps = 0;

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

        GameModeMod gameModeMod = new LastTeamStanding();
//        GameModeMod gameModeMod = new TeamDeathMatch();
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
        }
    }

    public void step() {
        super.step();

        long now = System.currentTimeMillis();
        delta = (float) ((now - lastTime) / MS_PER_TICK);
        lastTime = now;
        // Update world
        roomGame.step(delta);

        // Manages round time
        roundStep();

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
        sendToAll(new ChatPacket("[YELLOW]Round Started."));

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
        sendToAll(new ChatPacket("[YELLOW]Round Ended."));

        for (GameModeMod mod : mods) {
            mod.evRoundEnd();
        }
    }

    public void notifyModPlayerConnected(PlayerModInterface player) {
        sendToAll(new ChatPacket("[YELLOW]Player connected."));

        for (GameModeMod mod : mods) {
            mod.evPlayerConnected(player);
        }
    }

    public void notifyModPlayerJoinedTeam(PlayerModInterface player) {
        sendToAll(new ChatPacket("[YELLOW]Played joined team."));

        for (GameModeMod mod : mods) {
            mod.evPlayerJoinedTeam(player);
        }
    }

    public void notifyModPlayerDestroyed(PlayerModInterface player) {
        for (GameModeMod mod : mods) {
            mod.evPlayerDestroyed(player);
        }
    }

    public void notifyModPlayerKilled(PlayerModInterface playerKilled, PlayerModInterface playerKiller) {
        sendToAll(new ChatPacket("[YELLOW]Played killed."));

        for (GameModeMod mod : mods) {
            mod.evPlayerKilled(playerKiller, playerKilled);
        }
    }
}

