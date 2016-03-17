package com.nicktoony.cstopdown.networking.server;

import com.nicktoony.cstopdown.MyGame;
import com.nicktoony.cstopdown.networking.packets.Packet;
import com.nicktoony.cstopdown.networking.packets.TimestampedPacket;
import com.nicktoony.cstopdown.networking.packets.connection.AcceptPacket;
import com.nicktoony.cstopdown.networking.packets.connection.ConnectPacket;
import com.nicktoony.cstopdown.networking.packets.connection.LoadedPacket;
import com.nicktoony.cstopdown.networking.packets.connection.RejectPacket;
import com.nicktoony.cstopdown.networking.packets.game.CreatePlayerPacket;
import com.nicktoony.cstopdown.networking.packets.game.DestroyPlayerPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerInputPacket;
import com.nicktoony.cstopdown.networking.packets.player.PlayerUpdatePacket;
import com.nicktoony.cstopdown.rooms.game.entities.players.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by nick on 19/07/15.
 */
public abstract class SBClient {

    public enum STATE {
        INIT,
        CONNECTING,
        LOADING,
        SPECTATE,
        ALIVE,
        DISCONNECTING
    }

    private STATE state = STATE.INIT;
    private SBServer server;
    private Player player;
    private int tempCountdown = 200;
    private int id;
    private int lastUpdate = 0;
    private long initialTimestamp; // only sync'd on loaded!
    private List<TimestampedPacket> inputQueue = new ArrayList<TimestampedPacket>();
    private float leniency = 0;

    public abstract void sendPacket(Packet packet);
    public abstract void close();

    public SBClient(SBServer server) {
        this.server = server;
    }

    public void handleReceivedMessage(Packet packet) {
        switch (state) {
            case CONNECTING:
                handleConnectingMessages(packet);
                break;

            case LOADING:
                handleLoadingMessages(packet);
                break;

            case ALIVE:
                handleAliveMessages(packet);
                break;

        }
    }

    private void handleConnectingMessages(Packet packet) {
        if (packet instanceof ConnectPacket) {
            // TODO: connect request validation

            // And if successful..
            if (true) {
                this.sendPacket(new AcceptPacket(server.getConfig(), this.id));
                this.state = STATE.LOADING;
            } else {
                this.sendPacket(new RejectPacket());
                this.close();
            }
        }
    }

    private void handleLoadingMessages(Packet packet) {
        if (packet instanceof LoadedPacket) {
            this.state = STATE.SPECTATE;
            this.initialTimestamp = System.currentTimeMillis();
            fullUpdate();
        }
    }

    private void handleAliveMessages(Packet packet) {
        if (packet instanceof PlayerInputPacket) {
            insertInputQueue((PlayerInputPacket) packet);
        }
    }

    public void update() {
        if (state == STATE.SPECTATE) {
            if (tempCountdown > 0) {
                tempCountdown -= 1;
            } else {
                state = STATE.ALIVE;
                player = server.getGame().createPlayer(this.id, 50, 50);

                CreatePlayerPacket createPlayer = new CreatePlayerPacket();
                createPlayer.x = player.getX();
                createPlayer.y = player.getY();
                createPlayer.id = this.id;
                server.sendToAll(createPlayer);
            }
        } else if (state == STATE.ALIVE) {
            if (lastUpdate <= 0) {
                lastUpdate = 1000/server.getConfig().sv_tickrate;

                PlayerUpdatePacket packet = new PlayerUpdatePacket();
                packet.x = player.getX();
                packet.y = player.getY();
                packet.direction = player.getDirection();
                packet.id = id;
                packet.moveDown = player.getMoveDown();
                packet.moveUp = player.getMoveUp();
                packet.moveLeft = player.getMoveLeft();
                packet.moveRight = player.getMoveRight();
                server.sendToOthers(packet, this);

            } else {
                lastUpdate -= 1;
            }

            handleInputQueue();
        }


        if (leniency > 0) leniency -= 2;
    }

    private void handleInputQueue() {
        boolean inconsistent = false;
        ListIterator<TimestampedPacket> iterator = inputQueue.listIterator();
        while (iterator.hasNext()) {
            TimestampedPacket packet = iterator.next();
            // Wait until we've compensated for latency
            if (packet.timestamp <= getTimestamp() - server.getConfig().sv_lag_compensate) {
                // Latency has been compensated. Process it!
                iterator.remove();

                if (packet instanceof PlayerInputPacket) {
                    // Cast to input packet
                    PlayerInputPacket inputPacket = (PlayerInputPacket) packet;
                    // Update state of player
                    player.setMovement(inputPacket.moveUp, inputPacket.moveRight,
                            inputPacket.moveDown, inputPacket.moveLeft);
                    player.setDirection(inputPacket.direction);

                    // Calculate how much leniency we're providing
                    leniency += Math.abs(player.getX() - inputPacket.x)
                            + Math.abs(player.getY() - inputPacket.y);

                    // If leniency is within expected parameters
                    // Calculation: (1000/cl_tickrate) / (1000/SIMULATION_FPS)
                    // 16 is a good value for 4 updates a second..
                    if (leniency <= Math.max((1000/server.getConfig().cl_tickrate)
                            / (1000 / MyGame.GAME_FPS), 8) ) {
                        // Accept the clients simulation
                        player.setPosition(inputPacket.x, inputPacket.y);

                        // We should send an update to all players ASAP
                        lastUpdate = 0;
                    } else {
                        inconsistent = true;
                    }
                }

            } else {
                // Stop handling input, it's not old enough
                break;
            }
        }

        if (inconsistent) {
            // The client simulation is way off, correct them
            PlayerUpdatePacket fixPacket = new PlayerUpdatePacket();
            fixPacket.x = player.getX();
            fixPacket.y = player.getY();
            fixPacket.direction = player.getDirection();
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
        return player;
    }

    public void fullUpdate() {
        for (SBClient client : server.getClients()) {
            if (client != this && client.getState() == STATE.ALIVE) {
                CreatePlayerPacket packet = new CreatePlayerPacket();
                packet.id = client.getId();
                packet.x = client.getPlayer().getX();
                packet.y = client.getPlayer().getY();
                sendPacket(packet);
            }
        }
    }

    public long getTimestamp() {
        return (System.currentTimeMillis() - initialTimestamp);
    }

    public void insertInputQueue(TimestampedPacket packet) {
        // loop through all elements
        for (int i = 0; i < inputQueue.size(); i++) {
            // Skip to next one if smaller
            if (inputQueue.get(i).timestamp < packet.timestamp) continue;

            // Otherwise, found location
            inputQueue.add(i, packet);
            return;
        }
        // Jump to end
        inputQueue.add(packet);
    }

    /**
     * Event called when client is disconnected. Hence, should clean up
     */
    public void handleDisconnect() {
        state = STATE.DISCONNECTING;
        // Delete player if it exists
        if (player != null) {
            // Send packet to all
            DestroyPlayerPacket destroyPlayerPacket = new DestroyPlayerPacket();
            destroyPlayerPacket.id = id;
            server.sendToOthers(destroyPlayerPacket, this);

            // Remove the player from the room (which also disposes the object)
            server.getRoom().deleteRenderable(player);
            // No player
            player = null;
        }
    }
}
