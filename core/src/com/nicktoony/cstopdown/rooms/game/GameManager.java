package com.nicktoony.cstopdown.rooms.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.nicktoony.cstopdown.mods.gamemode.PlayerModInterface;
import com.nicktoony.cstopdown.networking.packets.game.*;
import com.nicktoony.cstopdown.networking.packets.helpers.PlayerDetailsWrapper;
import com.nicktoony.cstopdown.networking.packets.player.PlayerInputPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerSwitchWeapon;
import com.nicktoony.cstopdown.networking.packets.player.PlayerToggleLight;
import com.nicktoony.cstopdown.networking.packets.player.PlayerUpdatePacket;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;
import com.nicktoony.engine.networking.client.ClientSocket;
import com.nicktoony.engine.packets.Packet;
import com.nicktoony.engine.packets.connection.JoinTeamPacket;
import com.nicktoony.engine.packets.connection.LoadedPacket;
import com.nicktoony.engine.packets.connection.PingPacket;
import com.nicktoony.engine.rooms.RoomGame;
import com.nicktoony.engine.services.weapons.Weapon;

import java.util.*;

/**
 * Created by Nick on 03/01/2016.
 */
public class GameManager implements ClientSocket.SBSocketListener {


    private RoomGame roomGame;
    private ClientSocket socket;
    private Map<Integer, Player> playerIdMap = new LinkedHashMap<Integer, Player>();
    private Map<Integer, PlayerDetailsWrapper> playerDetailsMap = new LinkedHashMap<Integer, PlayerDetailsWrapper>();

    public OrderedMap<Integer, Float[]> storedPositions = new OrderedMap<Integer, Float[]>();
    public int number = 0;
    private boolean scoreboardChanged = true;
    private int team = PlayerModInterface.TEAM_SPECTATE;


    public int getInputNumber(float x, float y) {
        number ++;

        storedPositions.put(number - 1, new Float[] { x, y});

        return number - 1;
    }


    public GameManager(RoomGame roomGame, ClientSocket socket) {
        this.roomGame = roomGame;
        this.socket = socket;

        // Game manager is created when the map is loaded, so we're good to go!
        socket.sendMessage(new LoadedPacket());

        socket.prepareTimestamp();
    }

    /**
     * Calculates the correct timestamp relative to the time LoadedPacket was sent
     * @return long timestamp
     */
    public long getTimestamp() {
        return socket.getTimestamp();
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
        } else if (packet instanceof ChatPacket) {
            handleReceivedPacket((ChatPacket) packet);
        } else if (packet instanceof PlayerInputPacket) {
            handleReceivedPacket((PlayerInputPacket) packet);
        } else if (packet instanceof PlayerDetailsPacket) {
            handleReceivedPacket((PlayerDetailsPacket) packet);
        } else if (packet instanceof UpdateWeaponsPacket) {
            handleReceivedPacket((UpdateWeaponsPacket) packet);
        }
    }

    private void handleReceivedPacket(UpdateWeaponsPacket packet) {
        Player player = playerIdMap.get(packet.id);
        // If the player exists
        if (player != null) {
            player.overrideWeapons(packet.weapons, packet.slot);
//            if (player.getCurrentWeapon() != packet.slot) {
                player.setNextWeapon(packet.slot);
//            }
        }
    }

    private void handleReceivedPacket(PlayerDetailsPacket packet) {
        for (PlayerDetailsWrapper wrapper : packet.playerDetails) {
            PlayerDetailsWrapper existingWrapper  = playerDetailsMap.get(wrapper.id);
            if (existingWrapper == null) {
                existingWrapper = wrapper;
                playerDetailsMap.put(wrapper.id, existingWrapper);
                scoreboardChanged = true;
            } else {
                existingWrapper.name = wrapper.name;
                existingWrapper.kills = wrapper.kills;
                existingWrapper.deaths = wrapper.deaths;
                existingWrapper.ping = wrapper.ping;
                existingWrapper.team = wrapper.team;
                scoreboardChanged = true;
            }
        }

        for (int left : packet.left) {
            playerDetailsMap.remove(left);
            scoreboardChanged = true;
        }
    }

    private void handleReceivedPacket(PlayerInputPacket packet) {
// Find the player in question
        Player player = playerIdMap.get(packet.id);
        // If the player exists
        if (player != null) {
            player.setMovement(packet.moveUp, packet.moveRight, packet.moveDown, packet.moveLeft);
            player.setZoom(packet.zoom);
            player.setShooting(packet.shoot);
            player.setReloading(packet.reload);
            player.setDirection(packet.direction);

            resolveConflict(player, packet.x, packet.y);
//            System.out.println("INPUT");
        }
    }

    private void resolveConflict(Player player, float x, float y) {
        // Fix the desync by jumping to server position
        float xDiff = (x - player.getX())/8;
        float yDiff = (y - player.getY())/8;
//        if (Math.abs(xDiff + yDiff) > ALLOWANCE) {
            player.setPosition(player.getX() + xDiff, player.getY() + yDiff);
//        }

//        Vector2 from = player.getPosition();
//        Vector2 to = new Vector2(x, y);
//
//        if (from.dst(to) > 10) {
//            player.setPosition(player.getPosition().lerp(new Vector2(x, y), Math.max(0, Math.min(1f, (from.dst(to)-10) / 100f))));
//        }
//        } else {
//            player.setPosition(player.getPosition().lerp(new Vector2(x, y), .3f));

//        }
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
                // clear stored
                storedPositions.clear();

                roomGame.getMap().setEntitySnap(null);
                if (playerIdMap.get(packet.killer) != null) {
                    roomGame.getMap().setEntitySnap(playerIdMap.get(packet.killer));
                } else if (!playerIdMap.isEmpty()) {
                    roomGame.getMap().setEntitySnap(playerIdMap.values().iterator().next());
                }
            }

        }


        PlayerDetailsWrapper killedPlayer = getPlayerDetails(packet.id);

        if (killedPlayer != null) {
            PlayerDetailsWrapper killerPlayer = getPlayerDetails(packet.killer);

            // Set colours depending on team
            Color killerColor = killerPlayer != null && killerPlayer.team == PlayerModInterface.TEAM_CT ? Color.SKY : Color.CORAL;
            Color killedColor = killedPlayer.team == PlayerModInterface.TEAM_CT ? Color.SKY : Color.CORAL;

            ((CSHUD) this.roomGame.getHud()).addFrag(
                    new FragUI.Frag(
                            "[#" + killerColor + "]"
                                    + (killerPlayer != null ? killerPlayer.name : ""),
                            "[#" + killedColor + "]" + killedPlayer.name,
                            packet.cause));
        }

    }

    private void handleReceivedPacket(PlayerUpdatePacket packet) {
        // If it's not our player
        if (socket.getId() != packet.id) {
            // Find the player
            Player player = playerIdMap.get(packet.id);
            if (player != null) {
                resolveConflict(player, packet.x, packet.y);

                // Update other variables
                player.setDirection(packet.direction);
                // Update their inputs
                player.setMovement(packet.moveUp, packet.moveRight, packet.moveDown, packet.moveLeft);
                // Weapons
                player.setShooting(packet.shooting);
                player.setReloading(packet.reloading);
                player.setZoom(packet.zoom);
                player.setHealth(packet.health);
            }
        } else {
            // It's our player. We received this because our simulation desync'd too much
            Player player = playerIdMap.get(packet.id);
            if (player != null) {
                if (packet.health == -1) {
                    System.out.println("Server warned us of desync.");
                } else {
                    player.setHealth(packet.health);

                    float lx = 0;
                    float ly = 0;

                    if (packet.lastProcessed == -1) {
                        player.setX(packet.x);
                        player.setY(packet.y);
                    } else {
                        ObjectMap.Entries<Integer, Float[]> iterator = storedPositions.iterator();
                        Float[] found = null;
                        while (iterator.hasNext()) {
                            ObjectMap.Entry<Integer, Float[]> entry =  iterator.next();
                            if (entry.key < packet.lastProcessed) {
                                iterator.remove();
                                iterator.reset();
                            } else if (entry.key == packet.lastProcessed) {
                                found = entry.value;
                                lx = (packet.x - found[0])/8;
                                ly = (packet.y - found[1])/8;

//                                if (Math.abs(lx + ly) < 1) {
//                                    lx = ly = 0;
//                                }
                            } else {
                                if (found != null) {
                                    storedPositions.put(entry.key, new Float[] {
                                            entry.value[0] + lx,
                                            entry.value[1] + ly
                                    });
                                }
//                                break;
                            }
                        }

                        if (found != null) {
                            player.setX(player.getX() + lx);
                            player.setY(player.getY() + ly);
                        }

                        if (found == null
//                                || (Math.abs(player.getX()-packet.x) > 32
//                                || Math.abs(player.getY()-packet.y) > 32)
                                ) {
                            player.setX(packet.x);
                            player.setY(packet.y);
                        }
                    }

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
        player.overrideWeapons(packet.weapons, packet.currentWeapon);
        player.setNextWeapon(packet.currentWeapon);
        player.setTeam(packet.team);

        // Is it my player?
        if (socket != null && packet.id == socket.getId()) {
            team = packet.team;
        }
    }

    private int smartMod(int a, int b) {
        int mod = (a < 0) ? (b - (Math.abs(a) % b) ) %b : (a % b);
        return mod;
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

    private void handleReceivedPacket(ChatPacket packet) {
        ((CSHUD)this.roomGame.getHud()).addChatLine(packet.message);
    }

    private void handleReceivedPacket(PingPacket packet) {
        socket.sendMessage(packet);
    }

    public boolean isSpectating() {
        if (roomGame.getMap().getEntitySnap() == null) {
            return false;
        }
        return roomGame.getMap().getEntitySnap().getId() != socket.getId();
    }

    public PlayerDetailsWrapper getSpectatingPlayer() {
        if (isSpectating()) {
            return getPlayerDetails(roomGame.getMap().getEntitySnap().getId());
        }

        return null;
    }

    public void spectateNext() {
        if (isSpectating() && playerIdMap.size() > 0) {
            boolean next = false;
            boolean done = false;
            // For each player
            for (Map.Entry<Integer, Player> playerEntry : playerIdMap.entrySet()) {

                // If we're at the next in chain
                if (next) {
                    // Set entity snap
                    roomGame.getMap().setEntitySnap(playerEntry.getValue());
                    done = true;
                    break;
                } else {
                    // Check if this is current one
                    if (playerEntry.getKey() == roomGame.getMap().getEntitySnap().getId()) {
                        next = true;
                    }
                }
            }

            if (!done) {
                // wasn't another in chain.. probs end of list. let's wrap around
                roomGame.getMap().setEntitySnap(playerIdMap.entrySet().iterator().next().getValue());
            }
        }
    }

    public void spectatePrevious() {
        if (isSpectating() && playerIdMap.size() > 0) {
            Iterator<Map.Entry<Integer, Player>> iterator = playerIdMap.entrySet().iterator();
            Map.Entry<Integer, Player> previous = iterator.next();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Player> next = iterator.next();
                if (next.getKey() == roomGame.getMap().getEntitySnap().getId()) {
                    break;
                }
                previous = next;
            }

            roomGame.getMap().setEntitySnap(previous.getValue());
        }
    }

    public void disconnect() {
        socket.close();
    }

    public PlayerDetailsWrapper getPlayerDetails(int id) {
        PlayerDetailsWrapper wrapper = playerDetailsMap.get(id);
        return wrapper != null ? wrapper : new PlayerDetailsWrapper();
    }

    public Collection<PlayerDetailsWrapper> getPlayerDetails() {
        return playerDetailsMap.values();
    }

    public Collection<Player> getPlayers() {
        return playerIdMap.values();
    }

    public boolean getScoreboardChanged() {
        return scoreboardChanged;
    }

    public void setScoreboardChanged(boolean newValue) {
        this.scoreboardChanged = newValue;
    }

    public int getTeam() {
        return team;
    }

    public void joinTeam(int team) {
        socket.sendMessage(new JoinTeamPacket(team));
    }

    public void buyWeapon(Weapon weapon) {
        socket.sendMessage(new BuyWeaponPacket(weapon));
    }
}
