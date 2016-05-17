package com.nicktoony.cstopdown.rooms.game;

import com.nicktoony.cstopdown.networking.packets.game.CreatePlayerPacket;
import com.nicktoony.cstopdown.networking.packets.game.DestroyPlayerPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerSwitchWeapon;
import com.nicktoony.cstopdown.networking.packets.player.PlayerToggleLight;
import com.nicktoony.cstopdown.networking.packets.player.PlayerUpdatePacket;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.connection.LoadedPacket;
import com.nicktoony.engine.packets.connection.PingPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nick on 03/01/2016.
 */
public class GameManager implements ClientSocket.SBSocketListener {


    private RoomGame roomGame;
    private ClientSocket socket;
    private Map<Integer, Player> playerIdMap = new HashMap<Integer, Player>();
    private long initialTimestamp;


    public GameManager(RoomGame roomGame, ClientSocket socket) {
        this.roomGame = roomGame;
        this.socket = socket;

        // Game manager is created when the map is loaded, so we're good to go!
        socket.sendMessage(new LoadedPacket());

        initialTimestamp = System.currentTimeMillis();
    }

    /**
     * Calculates the correct timestamp relative to the time LoadedPacket was sent
     * @return long timestamp
     */
    public long getTimestamp() {
        return (System.currentTimeMillis() - initialTimestamp);
    }

    @Override
    public void onOpen(ClientSocket socket) {
        // will never see, as this animationEvent is called in RoomConnect
    }

    @Override
    public void onClose(ClientSocket socket) {
        // the RoomConnect setup a listener for us to deal with this situation
    }


    @Override
    public void onError(ClientSocket socket, Exception exception) {

    }

    @Override
    public void onMessage(ClientSocket socket, Packet packet) {
        if (packet instanceof CreatePlayerPacket) {
            handleReceivedPacket((CreatePlayerPacket) packet);
        } else if (packet instanceof PlayerUpdatePacket) {
            handleReceivedPacket((PlayerUpdatePacket) packet);
        } else if (packet instanceof DestroyPlayerPacket) {
            handleReceivedPacket((DestroyPlayerPacket) packet);
        } else if (packet instanceof PlayerToggleLight) {
            handleReceivedPacket((PlayerToggleLight) packet);
        } else if (packet instanceof PlayerSwitchWeapon) {
            handleReceivedPacket((PlayerSwitchWeapon) packet);
        } else if (packet instanceof PingPacket) {
            handleReceivedPacket((PingPacket) packet);
        }
    }

    private void handleReceivedPacket(DestroyPlayerPacket packet) {
        // Find the player in question
        Player player = playerIdMap.get(packet.id);
        // If the player exists
        if (player != null) {
            // Remove the player from room
            roomGame.deleteRenderable(player);

            // Remove the player from id list
            playerIdMap.remove(packet.id);

            // If it was the entity snap
            if (player == roomGame.getMap().getEntitySnap()) {
                roomGame.getMap().setEntitySnap(null);
                if (!playerIdMap.isEmpty()) {
                    roomGame.getMap().setEntitySnap(playerIdMap.values().iterator().next());
                }
            }
        }
    }

    private void handleReceivedPacket(PlayerUpdatePacket packet) {
        // If it's not our player
        if (socket.getId() != packet.id) {
            // Find the player
            Player player = playerIdMap.get(packet.id);
            if (player != null) {
                // Update their position and facing direction
                player.setPosition(packet.x, packet.y);
                player.setDirection(packet.direction);
                // Update their inputs
                player.setMovement(packet.moveUp, packet.moveRight, packet.moveDown, packet.moveLeft);
                // Weapons
                player.setShooting(packet.shooting);
                player.setReloading(packet.reloading);
            }
        } else {
            // It's our player. We received this because our simulation desync'd too much
            Player player = playerIdMap.get(packet.id);
            if (player != null) {
                // Fix the desync by jumping to server position
                float xDiff = (packet.x - player.getX())/2;
                float yDiff = (packet.y - player.getY())/2;
                if (Math.abs(xDiff + yDiff) > 4) {
                    player.setPosition(player.getX() + xDiff, player.getY() + yDiff);
                }

            }
        }
    }

    public void update() {

    }

    private void handleReceivedPacket(CreatePlayerPacket packet) {
        // Create the player in the room
        Player player = roomGame.createPlayer(packet.id,
                packet.x,
                packet.y, false);
        // Update light
        player.setLightOn(packet.light);
        // Add it to the ID-Player map
        playerIdMap.put(packet.id, player);
        // Weapon
        player.setWeapons(packet.weapons);
        player.setNextWeapon(packet.currentWeapon);
    }

    private void handleReceivedPacket(PlayerToggleLight packet) {
        // Find the player in question
        Player player = playerIdMap.get(packet.id);
        // If the player exists
        if (player != null) {
            // Set their light
            player.setLightOn(packet.light);
        }
    }

    private void handleReceivedPacket(PlayerSwitchWeapon packet) {
        // Find the player in question
        Player player = playerIdMap.get(packet.id);
        // If the player exists
        if (player != null) {
            // Set their light
            player.setNextWeapon(packet.slot);
        }
    }

    private void handleReceivedPacket(PingPacket packet) {
        socket.sendMessage(packet);
    }
}
