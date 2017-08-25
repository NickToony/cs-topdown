package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.mods.CSServerPlayerWrapper;
import com.nicktoony.cstopdown.networking.packets.game.BuyWeaponPacket;
import com.nicktoony.cstopdown.networking.packets.game.CreatePlayerPacket;
import com.nicktoony.cstopdown.networking.packets.game.DestroyPlayerPacket;
import com.nicktoony.cstopdown.networking.packets.game.PlayerDetailsPacket;
import com.nicktoony.cstopdown.networking.packets.helpers.PlayerDetailsWrapper;
import com.nicktoony.cstopdown.networking.packets.player.PlayerInputPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerSwitchWeapon;
import com.nicktoony.cstopdown.networking.packets.player.PlayerToggleLight;
import com.nicktoony.cstopdown.networking.packets.player.PlayerUpdatePacket;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.engine.EngineConfig;
import com.nicktoony.engine.networking.server.ServerClientHandler;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.TimestampedPacket;
import com.nicktoony.engine.packets.connection.ConnectPacket;
import com.nicktoony.engine.packets.connection.JoinTeamPacket;
import com.nicktoony.engine.packets.connection.LoadedPacket;
import com.nicktoony.engine.packets.connection.MapPacket;
import com.nicktoony.engine.services.weapons.Weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nick on 19/07/15.
 */
public abstract class CSServerClientHandler extends ServerClientHandler {


    protected CSServerPlayerWrapper player;
    protected CSServer server;
    private long lastUpdate = 0;
    private long lastInput = System.currentTimeMillis();
    private float leniency = 0;
    private int lastProcessed = -1;

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
        } else if (packet instanceof MapPacket) {
            // They want the map file
            MapPacket mapWrapper = new MapPacket();
            mapWrapper.map = server.getRoom().getMap().toString();
            mapWrapper.pixels = server.getRoom().getMap().getTilesetImages(server.getPlatformProvider());
            mapWrapper.tilesets = server.getRoom().getMap().getTilesetNames().toArray(new String[server.getRoom().getMap().getTilesetNames().size()]);
//            MapPacket mapPacket = new MapPacket();
//            mapPacket.mapWrapper = mapWrapper;
//            mapWrapper.pixels = null;
            sendPacket(mapWrapper);
        }


    }

    @Override
    protected void handleConnectingMessages(Packet packet) {
        super.handleConnectingMessages(packet);

        if (packet instanceof ConnectPacket) {
            player.getPlayerDetails().name = ((ConnectPacket)packet).name;
        }
    }

    public void update() {
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
                    packet.zoom = player.getPlayer().getZoomKey();
                    packet.health = player.getPlayer().getHealth();
                    packet.lastProcessed = lastProcessed;
                    server.sendToAll(packet);
//                    server.sendToOthers(packet, this);
//                    for (CSServerClientHandler client : server.getClients()) {
//                        if (client != this && client.getState() == ServerClientHandler.STATE.INGAME) {
//                            if (!client.getPlayerWrapper().isAlive() || !this.getPlayerWrapper().isAlive()) {
//                                client.sendPacket(packet);
//                            } else {
//                                if (client.getPlayer().canSeePlayer(getPlayer())) {
//                                    client.sendPacket(packet);
//                                }
//                            }
//                        }
//                    }

                }
            }

        super.update();

           if (leniency > 0) leniency -= 2;
           if (leniency > 100) leniency = 100;
        }

        player.update();
    }

    @Override
    protected void handleInput(TimestampedPacket packet) {
        if (!player.isAlive()) {
            return;
        }

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
            getPlayer().setZoom(inputPacket.zoom);

            // Store which input we're processing
            lastProcessed = inputPacket.number;

            // Time since last input was 0
            lastInput = System.currentTimeMillis();

            // Attach the id
            inputPacket.id = getID();
            // Attach the SERVER position
//            inputPacket.x = getPlayer().getX();
//            inputPacket.y = getPlayer().getY();
//            inputPacket.timestamp = getTimestamp();
//            server.sendToOthers(packet, this);

            // Trigger immediate update
            lastUpdate = -1;
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

    }

    @Override
    public void setId(int id) {
        super.setId(id);

        this.player.getPlayerDetails().id = id;
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

        List<PlayerDetailsWrapper> playerDetails = new ArrayList<PlayerDetailsWrapper>();
        for (CSServerClientHandler client : server.getClients()) {
            playerDetails.add(client.getPlayerWrapper().getPlayerDetails());
        }
        PlayerDetailsPacket detailsPacket = new PlayerDetailsPacket();
        detailsPacket.playerDetails = playerDetails.toArray(new PlayerDetailsWrapper[playerDetails.size()]);
        detailsPacket.left = new int[0];
        sendPacket(detailsPacket);

        for (CSServerClientHandler client : server.getClients()) {

            if (client.player.isAlive()) {
                CreatePlayerPacket packet = new CreatePlayerPacket();
                packet.id = client.getId();
                packet.x = client.getPlayer().getX();
                packet.y = client.getPlayer().getY();
                packet.light = client.getPlayer().isLightOn();
                packet.weapons = client.getPlayer().getWeapons();
                packet.currentWeapon = client.getPlayer().getNextWeapon();
                packet.team = client.getPlayerWrapper().getTeam();
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
        this.destroyPlayer(null);
    }

    public void destroyPlayer(CSServerPlayerWrapper killer) {
        // Send packet to all
        DestroyPlayerPacket destroyPlayerPacket = new DestroyPlayerPacket();
        destroyPlayerPacket.id = id;
        destroyPlayerPacket.killer = killer == null ? -1 : killer.getID();
        destroyPlayerPacket.cause = killer == null ? "SLAYED" : killer.getWeaponName();
//        destroyPlayerPacket.victimName = player.getName();
//        if (killer != null) {
//            destroyPlayerPacket.killerName = killer.getName();
//            destroyPlayerPacket.weapon = killer.getWeaponName();
//        }
        server.sendToAll(destroyPlayerPacket);
    }

    public void createPlayer() {

        CreatePlayerPacket createPlayer = new CreatePlayerPacket();
        createPlayer.x = player.getX();
        createPlayer.y = player.getY();
        createPlayer.id = this.id;
        createPlayer.weapons = getPlayer().getWeapons();
        createPlayer.currentWeapon = getPlayer().getCurrentWeapon();
        createPlayer.team = player.getTeam();
        server.sendToAll(createPlayer);
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void handleReceivedMessage(Packet packet) {
        super.handleReceivedMessage(packet);

        if (packet instanceof JoinTeamPacket) {
            JoinTeamPacket joinTeamPacket = (JoinTeamPacket) packet;
            if (EngineConfig.isValidTeam(joinTeamPacket.team)) {
                if (this.player.isAlive()) {
                    this.player.slay(true);
                }
                this.player.joinTeam((joinTeamPacket).team);
            }
        } else if (packet instanceof BuyWeaponPacket) {
            BuyWeaponPacket buyWeaponPacket = (BuyWeaponPacket) packet;
            if (this.player.isAlive()) {
                this.player.giveWeapon(buyWeaponPacket.getWeapon());
            }
        }
    }
}
