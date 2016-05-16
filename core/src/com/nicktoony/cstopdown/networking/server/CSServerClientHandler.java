package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.mods.CSServerPlayerWrapper;
import com.nicktoony.cstopdown.networking.packets.game.CreatePlayerPacket;
import com.nicktoony.cstopdown.networking.packets.game.DestroyPlayerPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerInputPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerSwitchWeapon;
import com.nicktoony.cstopdown.networking.packets.player.PlayerToggleLight;
import com.nicktoony.cstopdown.networking.packets.player.PlayerUpdatePacket;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.engine.MyGame;
import com.nicktoony.engine.networking.server.ServerClientHandler;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.TimestampedPacket;
import com.nicktoony.engine.packets.connection.LoadedPacket;

/**
 * Created by nick on 19/07/15.
 */
public abstract class CSServerClientHandler extends ServerClientHandler {


    protected CSServerPlayerWrapper player;
    protected CSServer server;
    private long lastUpdate = 0;
    private float leniency = 0;

    public CSServerClientHandler(CSServer server) {
        super(server);
        this.server = server;
        this.player = new CSServerPlayerWrapper(server, this) {
            @Override
            public int getID() {
                return getId();
            }

            @Override
            public boolean isBot() {
                return bot();
            }
        };
    }

    @Override
    protected void handleLoadingMessages(Packet packet) {
        if (packet instanceof LoadedPacket) {

            // Notify mods of a player joined
            server.notifyModPlayerConnected(player);

            super.handleLoadingMessages(packet);

            // update the player on all OTHER players
            fullUpdate();
        }


    }

    public void update() {
        super.update();

        if (state == STATE.INGAME) {
            if (player.isAlive()) {
                if (lastUpdate + (1000 / server.getConfig().sv_tickrate) < System.currentTimeMillis()) {
                    lastUpdate = System.currentTimeMillis();

                    PlayerUpdatePacket packet = new PlayerUpdatePacket();
                    packet.x = player.getX();
                    packet.y = player.getY();
                    packet.direction = player.getPlayer().getDirection();
                    packet.id = id;
                    packet.moveDown = player.getPlayer().getMoveDown();
                    packet.moveUp = player.getPlayer().getMoveUp();
                    packet.moveLeft = player.getPlayer().getMoveLeft();
                    packet.moveRight = player.getPlayer().getMoveRight();
                    packet.shooting = player.getPlayer().getShooting();
                    packet.reloading = player.getPlayer().getReloading();
                    server.sendToOthers(packet, this);

                }
            }

           if (leniency > 0) leniency -= 2;
           if (leniency > 100) leniency = 100;
        }
    }

    @Override
    protected void handleInput(TimestampedPacket packet) {
        boolean inconsistent = false;
        if (packet instanceof PlayerInputPacket) {
            // Cast to input packet
            PlayerInputPacket inputPacket = (PlayerInputPacket) packet;
            // Update state of player
            getPlayer().setMovement(inputPacket.moveUp, inputPacket.moveRight,
                    inputPacket.moveDown, inputPacket.moveLeft);
            getPlayer().setDirection(inputPacket.direction);

            // Update shooting
            getPlayer().setShooting(inputPacket.shoot);
            getPlayer().setReloading(inputPacket.reload);

            // Calculate how much leniency we're providing
            leniency += Math.abs(player.getX() - inputPacket.x)
                    + Math.abs(player.getY() - inputPacket.y);

            // If leniency is within expected parameters
            // Calculation: (1000/cl_tickrate) / (1000/SIMULATION_FPS)
            // 16 is a good value for 4 updates a second..
            if (leniency <= Math.max((1000/server.getConfig().cl_tickrate)
                    / (1000 / MyGame.GAME_FPS), 8) ) {
                // Accept the clients simulation
                getPlayer().setPosition(inputPacket.x, inputPacket.y);

                // We should send an update to all players ASAP
                lastUpdate = 0;
            } else {
                inconsistent = true;
            }
        } else if (packet instanceof PlayerToggleLight) {
            PlayerToggleLight lightPacket = (PlayerToggleLight) packet;
            getPlayer().setLightOn(lightPacket.light);

            // Add an id
            lightPacket.id = id;

            server.sendToOthers(lightPacket, this);
        } else if (packet instanceof PlayerSwitchWeapon) {
            PlayerSwitchWeapon playerSwitchWeapon = (PlayerSwitchWeapon) packet;
            getPlayer().setNextWeapon(playerSwitchWeapon.slot);

            // Add an id
            playerSwitchWeapon.id = id;

            server.sendToOthers(playerSwitchWeapon, this);
        }

        if (inconsistent) {
            // The client simulation is way off, correct them
            PlayerUpdatePacket fixPacket = new PlayerUpdatePacket();
            fixPacket.x = player.getX();
            fixPacket.y = player.getY();
            fixPacket.direction = player.getPlayer().getDirection();
            fixPacket.id = id;
            // We don't send movement.. the player knows that already
            sendPacket(fixPacket);
        }

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public STATE getState() {
        return state;
    }

    public void setState(STATE state) {
        this.state = state;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public CSServerPlayerWrapper getPlayerWrapper() {
        return player;
    }

    public void fullUpdate() {
        for (CSServerClientHandler client : server.getClients()) {
            if (client.player.isAlive()) {
                CreatePlayerPacket packet = new CreatePlayerPacket();
                packet.id = client.getId();
                packet.x = client.getPlayer().getX();
                packet.y = client.getPlayer().getY();
                packet.light = client.getPlayer().isLightOn();
                packet.weapons = client.getPlayer().getWeapons();
                packet.currentWeapon = client.getPlayer().getCurrentWeapon();
                sendPacket(packet);
            }
        }
    }

    /**
     * Event called when client is disconnected. Hence, should clean up
     */
    public void handleDisconnect() {
        // Delete player if it exists
        if (player != null) {
            destroyPlayer();
        }
    }

    public void destroyPlayer() {
        // Send packet to all
        DestroyPlayerPacket destroyPlayerPacket = new DestroyPlayerPacket();
        destroyPlayerPacket.id = id;
        server.sendToAll(destroyPlayerPacket);
    }

    public void createPlayer() {

        CreatePlayerPacket createPlayer = new CreatePlayerPacket();
        createPlayer.x = player.getX();
        createPlayer.y = player.getY();
        createPlayer.id = this.id;
        createPlayer.weapons = getPlayer().getWeapons();
        createPlayer.currentWeapon = getPlayer().getCurrentWeapon();
        server.sendToAll(createPlayer);
    }

    @Override
    public int getID() {
        return id;
    }
}